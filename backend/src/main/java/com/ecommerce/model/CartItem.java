package com.ecommerce.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class CartItem {
    private Long id;
    private Long cartId;
    private Long productId;
    private Integer quantity;
    private BigDecimal priceAtTime;
    private Map<String, Object> selectedVariants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // For joined data
    private Product product;
    private Cart cart;
    
    // Constructors
    public CartItem() {}
    
    public CartItem(Long cartId, Long productId, Integer quantity, BigDecimal priceAtTime) {
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public CartItem(Long cartId, Long productId, Integer quantity, BigDecimal priceAtTime, Map<String, Object> selectedVariants) {
        this(cartId, productId, quantity, priceAtTime);
        this.selectedVariants = selectedVariants;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCartId() { return cartId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity;
        this.updatedAt = LocalDateTime.now();
    }
    
    public BigDecimal getPriceAtTime() { return priceAtTime; }
    public void setPriceAtTime(BigDecimal priceAtTime) { this.priceAtTime = priceAtTime; }
    
    public Map<String, Object> getSelectedVariants() { return selectedVariants; }
    public void setSelectedVariants(Map<String, Object> selectedVariants) { this.selectedVariants = selectedVariants; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }
    
    // Utility methods
    public BigDecimal getTotalPrice() {
        return priceAtTime.multiply(BigDecimal.valueOf(quantity));
    }
    
    public boolean hasVariants() {
        return selectedVariants != null && !selectedVariants.isEmpty();
    }
    
    public void increaseQuantity(int amount) {
        this.quantity += amount;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void decreaseQuantity(int amount) {
        this.quantity = Math.max(0, this.quantity - amount);
        this.updatedAt = LocalDateTime.now();
    }
}