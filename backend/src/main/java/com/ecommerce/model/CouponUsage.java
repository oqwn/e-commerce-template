package com.ecommerce.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponUsage {
    private Long id;
    private Long couponId;
    private Long userId;
    private Long orderId;
    private BigDecimal discountAmount;
    private LocalDateTime createdAt;
    
    // For joined data
    private Coupon coupon;
    private User user;
    private Order order;
    
    // Constructors
    public CouponUsage() {}
    
    public CouponUsage(Long couponId, Long userId, Long orderId, BigDecimal discountAmount) {
        this.couponId = couponId;
        this.userId = userId;
        this.orderId = orderId;
        this.discountAmount = discountAmount;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Coupon getCoupon() { return coupon; }
    public void setCoupon(Coupon coupon) { this.coupon = coupon; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
}