package com.ecommerce.mapper;

import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface OrderMapper {
    
    // Order CRUD operations
    void insertOrder(Order order);
    void updateOrder(Order order);
    void deleteOrderById(@Param("id") Long id);
    Optional<Order> findOrderById(@Param("id") Long id);
    Optional<Order> findOrderByOrderNumber(@Param("orderNumber") String orderNumber);
    Optional<Order> findOrderWithItems(@Param("id") Long id);
    Optional<Order> findOrderWithItemsAndAddresses(@Param("id") Long id);
    
    // Order item CRUD operations
    void insertOrderItem(OrderItem orderItem);
    void updateOrderItem(OrderItem orderItem);
    void deleteOrderItemById(@Param("id") Long id);
    void deleteOrderItemsByOrderId(@Param("orderId") Long orderId);
    Optional<OrderItem> findOrderItemById(@Param("id") Long id);
    List<OrderItem> findOrderItemsByOrderId(@Param("orderId") Long orderId);
    List<OrderItem> findOrderItemsWithProductsByOrderId(@Param("orderId") Long orderId);
    
    // Order status management
    void updateOrderStatus(@Param("id") Long id, @Param("status") Order.OrderStatus status);
    void updatePaymentStatus(@Param("id") Long id, @Param("paymentStatus") Order.PaymentStatus paymentStatus);
    void markOrderAsDelivered(@Param("id") Long id, @Param("deliveredAt") LocalDateTime deliveredAt);
    void markOrderAsCancelled(@Param("id") Long id, @Param("cancelledAt") LocalDateTime cancelledAt);
    
    // User order queries
    List<Order> findOrdersByUserId(@Param("userId") Long userId);
    List<Order> findOrdersByUserIdWithPagination(@Param("userId") Long userId, 
                                                 @Param("offset") int offset, 
                                                 @Param("limit") int limit);
    List<Order> findOrdersByUserIdAndStatus(@Param("userId") Long userId, 
                                           @Param("status") Order.OrderStatus status);
    
    // Store order queries (for sellers)
    List<Order> findOrdersByStoreId(@Param("storeId") Long storeId);
    List<Order> findOrdersByStoreIdWithPagination(@Param("storeId") Long storeId,
                                                  @Param("offset") int offset,
                                                  @Param("limit") int limit);
    List<Order> findOrdersByStoreIdAndStatus(@Param("storeId") Long storeId,
                                            @Param("status") Order.OrderStatus status);
    
    // Admin order queries
    List<Order> findAllOrders();
    List<Order> findAllOrdersWithPagination(@Param("offset") int offset, @Param("limit") int limit);
    List<Order> findOrdersByStatus(@Param("status") Order.OrderStatus status);
    List<Order> findOrdersByPaymentStatus(@Param("paymentStatus") Order.PaymentStatus paymentStatus);
    List<Order> findOrdersCreatedBetween(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);
    
    // Order search
    List<Order> searchOrdersByOrderNumber(@Param("orderNumber") String orderNumber);
    List<Order> searchOrdersByUserEmail(@Param("email") String email);
    
    // Order statistics
    long countOrdersByUserId(@Param("userId") Long userId);
    long countOrdersByStoreId(@Param("storeId") Long storeId);
    long countAllOrders();
    long countOrdersByStatus(@Param("status") Order.OrderStatus status);
    long countOrdersByPaymentStatus(@Param("paymentStatus") Order.PaymentStatus paymentStatus);
    java.math.BigDecimal getTotalRevenueByStoreId(@Param("storeId") Long storeId);
    java.math.BigDecimal getTotalRevenueByUserId(@Param("userId") Long userId);
    java.math.BigDecimal getTotalRevenue();
    
    // Recent orders
    List<Order> findRecentOrdersByUserId(@Param("userId") Long userId, @Param("limit") int limit);
    List<Order> findRecentOrdersByStoreId(@Param("storeId") Long storeId, @Param("limit") int limit);
    List<Order> findRecentOrders(@Param("limit") int limit);
    
    // Order number generation
    String generateOrderNumber();
    boolean existsByOrderNumber(@Param("orderNumber") String orderNumber);
    
    // Check existence
    boolean existsOrderById(@Param("id") Long id);
}