package com.ecommerce.service;

import com.ecommerce.dto.CreateOrderRequest;
import com.ecommerce.dto.OrderResponse;
import com.ecommerce.dto.OrderItemResponse;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.mapper.CartMapper;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.mapper.AddressMapper;
import com.ecommerce.model.*;
import com.ecommerce.service.CouponService.CouponValidationResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {
    
    private final OrderMapper orderMapper;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    private final AddressMapper addressMapper;
    private final InventoryService inventoryService;
    private final CouponService couponService;
    
    public OrderService(OrderMapper orderMapper, CartMapper cartMapper, ProductMapper productMapper,
                       AddressMapper addressMapper, InventoryService inventoryService,
                       CouponService couponService) {
        this.orderMapper = orderMapper;
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
        this.addressMapper = addressMapper;
        this.inventoryService = inventoryService;
        this.couponService = couponService;
    }
    
    public OrderResponse createOrderFromCart(Long userId, String sessionId, CreateOrderRequest request) {
        // Get user's cart
        Cart cart = getUserCart(userId, sessionId);
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        
        // Validate addresses
        validateAddresses(userId, request.getShippingAddressId(), request.getBillingAddressId());
        
        // Validate inventory and calculate totals
        validateCartInventory(cart);
        OrderTotals totals = calculateOrderTotals(cart, request.getShippingMethodId(), request.getCouponCode(), userId);
        
        // Generate order number
        String orderNumber = generateOrderNumber();
        
        // Create order
        Order order = new Order(
            orderNumber,
            userId,
            totals.totalAmount,
            request.getShippingAddressId(),
            request.getBillingAddressId()
        );
        
        order.setSubtotalAmount(totals.subtotalAmount);
        order.setTaxAmount(totals.taxAmount);
        order.setShippingAmount(totals.shippingAmount);
        order.setDiscountAmount(totals.discountAmount);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNotes(request.getNotes());
        
        orderMapper.insertOrder(order);
        
        // Create order items from cart items
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            OrderItem orderItem = new OrderItem(
                order.getId(),
                cartItem.getProductId(),
                product.getStoreId(),
                cartItem.getQuantity(),
                cartItem.getPriceAtTime(),
                product.getName(),
                cartItem.getSelectedVariants()
            );
            
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                orderItem.setProductImageUrl(product.getImages().get(0).getImageUrl());
            }
            
            orderMapper.insertOrderItem(orderItem);
            
            // Reserve inventory
            inventoryService.reserveInventory(cartItem.getProductId(), cartItem.getQuantity(), 
                                            "Order " + orderNumber);
        }
        
        // Record coupon usage if coupon was applied
        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty() && 
            totals.discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            CouponValidationResult couponResult = couponService.validateAndCalculateDiscount(
                request.getCouponCode(), totals.subtotalAmount, userId, null);
            if (couponResult.isValid() && couponResult.getCoupon() != null) {
                couponService.recordCouponUsage(couponResult.getCoupon().getId(), userId, 
                    order.getId(), totals.discountAmount);
            }
        }
        
        // Clear cart after successful order creation
        cartMapper.deleteCartItemsByCartId(cart.getId());
        
        return getOrderResponse(order.getId());
    }
    
    public OrderResponse createDirectOrder(Long userId, CreateOrderRequest request) {
        // Validate addresses
        validateAddresses(userId, request.getShippingAddressId(), request.getBillingAddressId());
        
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Order items cannot be empty");
        }
        
        // Validate inventory and calculate totals
        validateDirectOrderInventory(request.getItems());
        OrderTotals totals = calculateDirectOrderTotals(request.getItems(), request.getShippingMethodId(), request.getCouponCode(), userId);
        
        // Generate order number
        String orderNumber = generateOrderNumber();
        
        // Create order
        Order order = new Order(
            orderNumber,
            userId,
            totals.totalAmount,
            request.getShippingAddressId(),
            request.getBillingAddressId()
        );
        
        order.setSubtotalAmount(totals.subtotalAmount);
        order.setTaxAmount(totals.taxAmount);
        order.setShippingAmount(totals.shippingAmount);
        order.setDiscountAmount(totals.discountAmount);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNotes(request.getNotes());
        
        orderMapper.insertOrder(order);
        
        // Create order items
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productMapper.findById(itemRequest.getProductId());
            if (product == null) {
                throw new RuntimeException("Product not found: " + itemRequest.getProductId());
            }
            OrderItem orderItem = new OrderItem(
                order.getId(),
                itemRequest.getProductId(),
                product.getStoreId(),
                itemRequest.getQuantity(),
                product.getPrice(),
                product.getName(),
                itemRequest.getSelectedVariants()
            );
            
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                orderItem.setProductImageUrl(product.getImages().get(0).getImageUrl());
            }
            
            orderMapper.insertOrderItem(orderItem);
            
            // Reserve inventory
            inventoryService.reserveInventory(itemRequest.getProductId(), itemRequest.getQuantity(), 
                                            "Order " + orderNumber);
        }
        
        // Record coupon usage if coupon was applied
        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty() && 
            totals.discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            CouponValidationResult couponResult = couponService.validateAndCalculateDiscount(
                request.getCouponCode(), totals.subtotalAmount, userId, null);
            if (couponResult.isValid() && couponResult.getCoupon() != null) {
                couponService.recordCouponUsage(couponResult.getCoupon().getId(), userId, 
                    order.getId(), totals.discountAmount);
            }
        }
        
        return getOrderResponse(order.getId());
    }
    
    public OrderResponse getOrderById(Long orderId) {
        return getOrderResponse(orderId);
    }
    
    public OrderResponse getOrderByNumber(String orderNumber) {
        Optional<Order> orderOpt = orderMapper.findOrderByOrderNumber(orderNumber);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found");
        }
        return getOrderResponse(orderOpt.get().getId());
    }
    
    public List<OrderResponse> getUserOrders(Long userId, int page, int size) {
        int offset = page * size;
        List<Order> orders = orderMapper.findOrdersByUserIdWithPagination(userId, offset, size);
        return orders.stream()
            .map(order -> convertToOrderResponse(order))
            .collect(Collectors.toList());
    }
    
    public List<OrderResponse> getStoreOrders(Long storeId, int page, int size) {
        int offset = page * size;
        List<Order> orders = orderMapper.findOrdersByStoreIdWithPagination(storeId, offset, size);
        return orders.stream()
            .map(order -> convertToOrderResponse(order))
            .collect(Collectors.toList());
    }
    
    public OrderResponse updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        Optional<Order> orderOpt = orderMapper.findOrderById(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found");
        }
        
        Order order = orderOpt.get();
        
        // Validate status transition
        validateStatusTransition(order.getStatus(), newStatus);
        
        orderMapper.updateOrderStatus(orderId, newStatus);
        
        // Handle inventory changes based on status
        handleInventoryOnStatusChange(order, newStatus);
        
        return getOrderResponse(orderId);
    }
    
    public OrderResponse cancelOrder(Long orderId, Long userId) {
        Optional<Order> orderOpt = orderMapper.findOrderById(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found");
        }
        
        Order order = orderOpt.get();
        
        // Verify user ownership
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Order does not belong to user");
        }
        
        if (!order.canBeCancelled()) {
            throw new RuntimeException("Order cannot be cancelled");
        }
        
        orderMapper.markOrderAsCancelled(orderId, LocalDateTime.now());
        
        // Release reserved inventory
        List<OrderItem> items = orderMapper.findOrderItemsByOrderId(orderId);
        for (OrderItem item : items) {
            inventoryService.releaseReservedInventory(item.getProductId(), item.getQuantity(), 
                                                    "Order cancelled: " + order.getOrderNumber());
        }
        
        return getOrderResponse(orderId);
    }
    
    private Cart getUserCart(Long userId, String sessionId) {
        if (userId != null) {
            return cartMapper.findCartByUserIdWithItems(userId).orElse(null);
        } else {
            return cartMapper.findCartBySessionIdWithItems(sessionId).orElse(null);
        }
    }
    
    private void validateAddresses(Long userId, Long shippingAddressId, Long billingAddressId) {
        // Validate shipping address
        Optional<Address> shippingAddress = addressMapper.findById(shippingAddressId);
        if (shippingAddress.isEmpty() || !shippingAddress.get().getUserId().equals(userId)) {
            throw new RuntimeException("Invalid shipping address");
        }
        
        // Validate billing address
        Optional<Address> billingAddress = addressMapper.findById(billingAddressId);
        if (billingAddress.isEmpty() || !billingAddress.get().getUserId().equals(userId)) {
            throw new RuntimeException("Invalid billing address");
        }
    }
    
    private void validateCartInventory(Cart cart) {
        for (CartItem item : cart.getItems()) {
            Product product = productMapper.findById(item.getProductId());
            if (product == null) {
                throw new RuntimeException("Product not found: " + item.getProductId());
            }
            if (product.getStatus() != Product.ProductStatus.ACTIVE) {
                throw new RuntimeException("Product is no longer available: " + product.getName());
            }
            
            if (product.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for " + product.getName() + 
                                         ". Available: " + product.getQuantity());
            }
        }
    }
    
    private void validateDirectOrderInventory(List<CreateOrderRequest.OrderItemRequest> items) {
        for (CreateOrderRequest.OrderItemRequest item : items) {
            Product product = productMapper.findById(item.getProductId());
            if (product == null) {
                throw new RuntimeException("Product not found: " + item.getProductId());
            }
            if (product.getStatus() != Product.ProductStatus.ACTIVE) {
                throw new RuntimeException("Product is no longer available: " + product.getName());
            }
            
            if (product.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for " + product.getName() + 
                                         ". Available: " + product.getQuantity());
            }
        }
    }
    
    private OrderTotals calculateOrderTotals(Cart cart, Long shippingMethodId, String couponCode, Long userId) {
        BigDecimal subtotal = BigDecimal.valueOf(cart.getTotalPrice());
        BigDecimal shipping = calculateShipping(subtotal, shippingMethodId);
        
        // Apply coupon discount
        BigDecimal discount = BigDecimal.ZERO;
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            CouponValidationResult couponResult = couponService.validateAndCalculateDiscount(
                couponCode, subtotal, userId, null); // TODO: Pass storeId when multi-store is supported
            if (couponResult.isValid()) {
                discount = couponResult.getDiscountAmount();
            }
        }
        
        BigDecimal tax = calculateTax(subtotal.subtract(discount));
        BigDecimal total = subtotal.add(shipping).add(tax).subtract(discount);
        
        return new OrderTotals(subtotal, tax, shipping, discount, total);
    }
    
    private OrderTotals calculateDirectOrderTotals(List<CreateOrderRequest.OrderItemRequest> items, 
                                                  Long shippingMethodId, String couponCode, Long userId) {
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (CreateOrderRequest.OrderItemRequest item : items) {
            Product product = productMapper.findById(item.getProductId());
            if (product != null) {
                BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                subtotal = subtotal.add(itemTotal);
            }
        }
        
        BigDecimal shipping = calculateShipping(subtotal, shippingMethodId);
        
        // Apply coupon discount
        BigDecimal discount = BigDecimal.ZERO;
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            CouponValidationResult couponResult = couponService.validateAndCalculateDiscount(
                couponCode, subtotal, userId, null); // TODO: Pass storeId when multi-store is supported
            if (couponResult.isValid()) {
                discount = couponResult.getDiscountAmount();
            }
        }
        
        BigDecimal tax = calculateTax(subtotal.subtract(discount));
        BigDecimal total = subtotal.add(shipping).add(tax).subtract(discount);
        
        return new OrderTotals(subtotal, tax, shipping, discount, total);
    }
    
    private BigDecimal calculateShipping(BigDecimal subtotal, Long shippingMethodId) {
        // Free shipping over $50
        if (subtotal.compareTo(BigDecimal.valueOf(50)) >= 0) {
            return BigDecimal.ZERO;
        }
        // TODO: Get actual shipping method price
        return BigDecimal.valueOf(5.99);
    }
    
    private BigDecimal calculateTax(BigDecimal taxableAmount) {
        // Simple 8.5% tax calculation
        return taxableAmount.multiply(BigDecimal.valueOf(0.085)).setScale(2, java.math.RoundingMode.HALF_UP);
    }
    
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = String.valueOf((int)(Math.random() * 1000));
        return "ORD-" + timestamp + "-" + randomSuffix;
    }
    
    private void validateStatusTransition(Order.OrderStatus currentStatus, Order.OrderStatus newStatus) {
        // Define valid status transitions
        switch (currentStatus) {
            case PENDING:
                if (newStatus != Order.OrderStatus.CONFIRMED && newStatus != Order.OrderStatus.CANCELLED) {
                    throw new RuntimeException("Invalid status transition from PENDING to " + newStatus);
                }
                break;
            case CONFIRMED:
                if (newStatus != Order.OrderStatus.PROCESSING && newStatus != Order.OrderStatus.CANCELLED) {
                    throw new RuntimeException("Invalid status transition from CONFIRMED to " + newStatus);
                }
                break;
            case PROCESSING:
                if (newStatus != Order.OrderStatus.SHIPPED && newStatus != Order.OrderStatus.CANCELLED) {
                    throw new RuntimeException("Invalid status transition from PROCESSING to " + newStatus);
                }
                break;
            case SHIPPED:
                if (newStatus != Order.OrderStatus.DELIVERED) {
                    throw new RuntimeException("Invalid status transition from SHIPPED to " + newStatus);
                }
                break;
            case DELIVERED:
            case CANCELLED:
            case REFUNDED:
                throw new RuntimeException("Cannot change status from " + currentStatus);
        }
    }
    
    private void handleInventoryOnStatusChange(Order order, Order.OrderStatus newStatus) {
        List<OrderItem> items = orderMapper.findOrderItemsByOrderId(order.getId());
        
        switch (newStatus) {
            case CANCELLED:
                // Release reserved inventory
                for (OrderItem item : items) {
                    inventoryService.releaseReservedInventory(item.getProductId(), item.getQuantity(), 
                                                            "Order cancelled: " + order.getOrderNumber());
                }
                break;
            case CONFIRMED:
                // Confirm inventory reservation (convert to committed)
                for (OrderItem item : items) {
                    inventoryService.commitReservedInventory(item.getProductId(), item.getQuantity(), 
                                                           "Order confirmed: " + order.getOrderNumber());
                }
                break;
        }
    }
    
    private OrderResponse getOrderResponse(Long orderId) {
        Optional<Order> orderOpt = orderMapper.findOrderWithItemsAndAddresses(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found");
        }
        return convertToOrderResponse(orderOpt.get());
    }
    
    private OrderResponse convertToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse(
            order.getId(),
            order.getOrderNumber(),
            order.getStatus().toString(),
            order.getPaymentStatus().toString(),
            order.getTotalAmount(),
            order.getCreatedAt()
        );
        
        response.setSubtotalAmount(order.getSubtotalAmount());
        response.setTaxAmount(order.getTaxAmount());
        response.setShippingAmount(order.getShippingAmount());
        response.setDiscountAmount(order.getDiscountAmount());
        response.setShippingAddress(convertAddressToDto(order.getShippingAddress()));
        response.setBillingAddress(convertAddressToDto(order.getBillingAddress()));
        response.setPaymentMethod(order.getPaymentMethod());
        response.setNotes(order.getNotes());
        response.setEstimatedDeliveryDate(order.getEstimatedDeliveryDate());
        response.setDeliveredAt(order.getDeliveredAt());
        response.setCancelledAt(order.getCancelledAt());
        response.setUpdatedAt(order.getUpdatedAt());
        
        if (order.getItems() != null) {
            List<OrderItemResponse> items = order.getItems().stream()
                .map(this::convertToOrderItemResponse)
                .collect(Collectors.toList());
            response.setItems(items);
            response.setTotalItems(order.getTotalItems());
        }
        
        return response;
    }
    
    private OrderItemResponse convertToOrderItemResponse(OrderItem item) {
        return new OrderItemResponse(
            item.getId(),
            item.getProductId(),
            item.getProductName(),
            item.getProduct() != null ? item.getProduct().getSlug() : "",
            item.getProductImageUrl(),
            item.getStore() != null ? item.getStore().getStoreName() : "Unknown Store",
            item.getStoreId(),
            item.getQuantity(),
            item.getPriceAtTime(),
            item.getTotalPrice()
        );
    }
    
    private com.ecommerce.dto.AddressDto convertAddressToDto(Address address) {
        if (address == null) return null;
        return com.ecommerce.dto.AddressDto.from(address);
    }
    
    private static class OrderTotals {
        final BigDecimal subtotalAmount;
        final BigDecimal taxAmount;
        final BigDecimal shippingAmount;
        final BigDecimal discountAmount;
        final BigDecimal totalAmount;
        
        OrderTotals(BigDecimal subtotalAmount, BigDecimal taxAmount, BigDecimal shippingAmount, 
                   BigDecimal discountAmount, BigDecimal totalAmount) {
            this.subtotalAmount = subtotalAmount;
            this.taxAmount = taxAmount;
            this.shippingAmount = shippingAmount;
            this.discountAmount = discountAmount;
            this.totalAmount = totalAmount;
        }
    }
}