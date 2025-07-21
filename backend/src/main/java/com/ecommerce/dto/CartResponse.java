package com.ecommerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CartResponse {
    private Long id;
    private List<CartItemResponse> items;
    private Integer totalItems;
    private BigDecimal totalPrice;
    private BigDecimal subtotal;
    private BigDecimal estimatedTax;
    private BigDecimal estimatedShipping;
    private LocalDateTime updatedAt;
    
    // Constructors
    public CartResponse() {}
    
    public CartResponse(Long id, List<CartItemResponse> items, Integer totalItems, 
                       BigDecimal totalPrice, LocalDateTime updatedAt) {
        this.id = id;
        this.items = items;
        this.totalItems = totalItems;
        this.totalPrice = totalPrice;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public List<CartItemResponse> getItems() { return items; }
    public void setItems(List<CartItemResponse> items) { this.items = items; }
    
    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
    
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getEstimatedTax() { return estimatedTax; }
    public void setEstimatedTax(BigDecimal estimatedTax) { this.estimatedTax = estimatedTax; }
    
    public BigDecimal getEstimatedShipping() { return estimatedShipping; }
    public void setEstimatedShipping(BigDecimal estimatedShipping) { this.estimatedShipping = estimatedShipping; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}