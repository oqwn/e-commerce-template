package com.ecommerce.service;

import com.ecommerce.dto.AddToCartRequest;
import com.ecommerce.dto.CartResponse;
import com.ecommerce.dto.CartItemResponse;
import com.ecommerce.dto.UpdateCartItemRequest;
import com.ecommerce.dto.response.FlashSaleProductResponse;
import com.ecommerce.mapper.CartMapper;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.service.FlashSaleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {
    
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    private final FlashSaleService flashSaleService;
    private final ObjectMapper objectMapper;
    
    public CartService(CartMapper cartMapper, ProductMapper productMapper, FlashSaleService flashSaleService, ObjectMapper objectMapper) {
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
        this.flashSaleService = flashSaleService;
        this.objectMapper = objectMapper;
    }
    
    public CartResponse getOrCreateCart(Long userId, String sessionId) {
        Cart cart;
        
        if (userId != null) {
            // For logged-in users
            Optional<Cart> existingCart = cartMapper.findCartByUserIdWithItems(userId);
            if (existingCart.isPresent()) {
                cart = existingCart.get();
            } else {
                cart = new Cart(userId, null);
                cartMapper.insertCart(cart);
                cart = cartMapper.findCartByUserIdWithItems(userId).orElse(cart);
            }
        } else {
            // For guest users
            Optional<Cart> existingCart = cartMapper.findCartBySessionIdWithItems(sessionId);
            if (existingCart.isPresent() && !existingCart.get().isExpired()) {
                cart = existingCart.get();
            } else {
                cart = new Cart(null, sessionId);
                cartMapper.insertCart(cart);
                cart = cartMapper.findCartBySessionIdWithItems(sessionId).orElse(cart);
            }
        }
        
        return convertToCartResponse(cart);
    }
    
    public CartResponse addToCart(Long userId, String sessionId, AddToCartRequest request) {
        // Validate product exists and is active
        Product product = productMapper.findByIdWithStats(request.getProductId());
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        if (product.getStatus() != Product.ProductStatus.ACTIVE) {
            throw new RuntimeException("Product is not available");
        }
        
        // Check inventory
        Integer availableQuantity = product.getQuantity();
        if (availableQuantity != null && availableQuantity < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock. Available: " + availableQuantity);
        }
        
        Cart cart = getOrCreateCartEntity(userId, sessionId);
        
        // Check if item with same product and variants already exists
        String variantsJson = convertVariantsToJson(request.getSelectedVariants());
        Optional<CartItem> existingItem = cartMapper.findCartItemByCartProductAndVariants(
            cart.getId(), request.getProductId(), variantsJson
        );
        
        if (existingItem.isPresent()) {
            // Update existing item quantity
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            
            // Check inventory for new quantity
            if (availableQuantity != null && availableQuantity < newQuantity) {
                throw new RuntimeException("Insufficient stock for requested quantity. Available: " + availableQuantity);
            }
            
            item.setQuantity(newQuantity);
            // Update price to current effective price (handles flash sale price changes)
            item.setPriceAtTime(getEffectivePrice(product));
            cartMapper.updateCartItem(item);
        } else {
            // Create new cart item
            BigDecimal effectivePrice = getEffectivePrice(product);
            CartItem cartItem = new CartItem(
                cart.getId(),
                request.getProductId(),
                request.getQuantity(),
                effectivePrice,
                request.getSelectedVariants()
            );
            cartMapper.insertCartItem(cartItem);
        }
        
        // Update cart timestamp
        cartMapper.updateCartTimestamp(cart.getId(), LocalDateTime.now());
        
        return getOrCreateCart(userId, sessionId);
    }
    
    public CartResponse updateCartItem(Long userId, String sessionId, Long cartItemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCartEntity(userId, sessionId);
        
        Optional<CartItem> cartItemOpt = cartMapper.findCartItemById(cartItemId);
        if (cartItemOpt.isEmpty()) {
            throw new RuntimeException("Cart item not found");
        }
        
        CartItem cartItem = cartItemOpt.get();
        
        // Verify the cart item belongs to the user's cart
        if (!cartItem.getCartId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to user");
        }
        
        if (request.getQuantity() <= 0) {
            // Remove item if quantity is 0 or negative
            cartMapper.deleteCartItemById(cartItemId);
        } else {
            // Validate inventory
            Product product = productMapper.findByIdWithStats(cartItem.getProductId());
            if (product != null) {
                Integer availableQuantity = product.getQuantity();
                if (availableQuantity != null && availableQuantity < request.getQuantity()) {
                    throw new RuntimeException("Insufficient stock. Available: " + availableQuantity);
                }
            }
            
            cartItem.setQuantity(request.getQuantity());
            cartMapper.updateCartItem(cartItem);
        }
        
        // Update cart timestamp
        cartMapper.updateCartTimestamp(cart.getId(), LocalDateTime.now());
        
        return getOrCreateCart(userId, sessionId);
    }
    
    public CartResponse removeFromCart(Long userId, String sessionId, Long cartItemId) {
        Cart cart = getOrCreateCartEntity(userId, sessionId);
        
        Optional<CartItem> cartItemOpt = cartMapper.findCartItemById(cartItemId);
        if (cartItemOpt.isEmpty()) {
            throw new RuntimeException("Cart item not found");
        }
        
        CartItem cartItem = cartItemOpt.get();
        
        // Verify the cart item belongs to the user's cart
        if (!cartItem.getCartId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to user");
        }
        
        cartMapper.deleteCartItemById(cartItemId);
        
        // Update cart timestamp
        cartMapper.updateCartTimestamp(cart.getId(), LocalDateTime.now());
        
        return getOrCreateCart(userId, sessionId);
    }
    
    public void clearCart(Long userId, String sessionId) {
        Cart cart = getOrCreateCartEntity(userId, sessionId);
        cartMapper.deleteCartItemsByCartId(cart.getId());
        cartMapper.updateCartTimestamp(cart.getId(), LocalDateTime.now());
    }
    
    public void transferGuestCartToUser(String sessionId, Long userId) {
        Optional<Cart> guestCartOpt = cartMapper.findCartBySessionId(sessionId);
        if (guestCartOpt.isEmpty()) {
            return; // No guest cart to transfer
        }
        
        Optional<Cart> userCartOpt = cartMapper.findCartByUserId(userId);
        if (userCartOpt.isPresent()) {
            // Merge guest cart into existing user cart
            cartMapper.mergeCartsOnLogin(guestCartOpt.get().getId(), userCartOpt.get().getId());
            cartMapper.deleteCartById(guestCartOpt.get().getId());
        } else {
            // Transfer guest cart to user
            cartMapper.transferGuestCartToUser(sessionId, userId);
        }
    }
    
    public void cleanupExpiredCarts() {
        LocalDateTime expiredBefore = LocalDateTime.now();
        cartMapper.clearExpiredCarts(expiredBefore);
    }
    
    private Cart getOrCreateCartEntity(Long userId, String sessionId) {
        if (userId != null) {
            Optional<Cart> cart = cartMapper.findCartByUserId(userId);
            if (cart.isPresent()) {
                return cart.get();
            } else {
                Cart newCart = new Cart(userId, null);
                cartMapper.insertCart(newCart);
                return cartMapper.findCartByUserId(userId).orElse(newCart);
            }
        } else {
            Optional<Cart> cart = cartMapper.findCartBySessionId(sessionId);
            if (cart.isPresent() && !cart.get().isExpired()) {
                return cart.get();
            } else {
                Cart newCart = new Cart(null, sessionId);
                cartMapper.insertCart(newCart);
                return cartMapper.findCartBySessionId(sessionId).orElse(newCart);
            }
        }
    }
    
    private CartResponse convertToCartResponse(Cart cart) {
        if (cart == null || cart.getItems() == null) {
            return new CartResponse(null, List.of(), 0, BigDecimal.ZERO, LocalDateTime.now());
        }
        
        List<CartItemResponse> items = cart.getItems().stream()
            .map(this::convertToCartItemResponse)
            .collect(Collectors.toList());
        
        int totalItems = cart.getTotalItems();
        BigDecimal totalPrice = BigDecimal.valueOf(cart.getTotalPrice());
        
        CartResponse response = new CartResponse(cart.getId(), items, totalItems, totalPrice, cart.getUpdatedAt());
        response.setSubtotal(totalPrice);
        response.setEstimatedTax(calculateEstimatedTax(totalPrice));
        response.setEstimatedShipping(calculateEstimatedShipping(totalPrice));
        
        return response;
    }
    
    private CartItemResponse convertToCartItemResponse(CartItem cartItem) {
        Product product = cartItem.getProduct();
        
        return new CartItemResponse(
            cartItem.getId(),
            cartItem.getProductId(),
            product != null ? product.getName() : "Unknown Product",
            product != null ? product.getSlug() : "",
            product != null && product.getImages() != null && !product.getImages().isEmpty() ? product.getImages().get(0).getImageUrl() : null,
            product != null && product.getStore() != null ? product.getStore().getStoreName() : "Unknown Store",
            product != null ? product.getStoreId() : null,
            cartItem.getQuantity(),
            cartItem.getPriceAtTime(),
            cartItem.getTotalPrice(),
            null, // cartItem.getSelectedVariants(), // TODO: Fix JSON handling
            product != null && product.getQuantity() != null && product.getQuantity() > 0,
            product != null && product.getQuantity() != null ? product.getQuantity() : 0,
            cartItem.getCreatedAt()
        );
    }
    
    private String convertVariantsToJson(Map<String, Object> variants) {
        if (variants == null || variants.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(variants);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    
    private BigDecimal calculateEstimatedTax(BigDecimal subtotal) {
        // Simple 8.5% tax calculation - this should be configurable
        return subtotal.multiply(BigDecimal.valueOf(0.085)).setScale(2, java.math.RoundingMode.HALF_UP);
    }
    
    private BigDecimal calculateEstimatedShipping(BigDecimal subtotal) {
        // Free shipping over $50, otherwise $5.99
        if (subtotal.compareTo(BigDecimal.valueOf(50)) >= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(5.99);
    }
    
    public String generateGuestSessionId() {
        return "guest_" + UUID.randomUUID().toString();
    }
    
    /**
     * Get the effective price for a product, considering flash sales
     */
    private BigDecimal getEffectivePrice(Product product) {
        try {
            // Check if product has an active flash sale
            FlashSaleProductResponse flashSale = flashSaleService.getActiveFlashSaleByProductId(product.getId());
            if (flashSale != null && flashSale.getSalePrice() != null) {
                return flashSale.getSalePrice();
            }
        } catch (Exception e) {
            // Log the error but continue with regular price
            // In production, you might want to use a proper logger
            System.err.println("Error checking flash sale for product " + product.getId() + ": " + e.getMessage());
        }
        
        // Return regular price if no flash sale or error occurred
        return product.getPrice();
    }
}