package com.ecommerce.model;

import java.time.LocalDateTime;
import java.util.List;

public class Cart {
    private Long id;
    private Long userId;
    private String sessionId;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // For joined data
    private List<CartItem> items;
    private User user;
    
    // Constructors
    public Cart() {}
    
    public Cart(Long userId, String sessionId) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        // Set expiration to 30 days from now for guest carts
        if (sessionId != null) {
            this.expiresAt = LocalDateTime.now().plusDays(30);
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    // Utility methods
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isGuestCart() {
        return sessionId != null && userId == null;
    }
    
    public int getTotalItems() {
        return items != null ? items.stream().mapToInt(CartItem::getQuantity).sum() : 0;
    }
    
    public double getTotalPrice() {
        return items != null ? items.stream()
            .mapToDouble(item -> item.getPriceAtTime().doubleValue() * item.getQuantity())
            .sum() : 0.0;
    }
}