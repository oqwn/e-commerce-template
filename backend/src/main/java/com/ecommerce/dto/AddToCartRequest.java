package com.ecommerce.dto;

import java.util.Map;

public class AddToCartRequest {
    private Long productId;
    private Integer quantity;
    private Map<String, Object> selectedVariants;
    
    // Constructors
    public AddToCartRequest() {}
    
    public AddToCartRequest(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
    
    public AddToCartRequest(Long productId, Integer quantity, Map<String, Object> selectedVariants) {
        this.productId = productId;
        this.quantity = quantity;
        this.selectedVariants = selectedVariants;
    }
    
    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public Map<String, Object> getSelectedVariants() { return selectedVariants; }
    public void setSelectedVariants(Map<String, Object> selectedVariants) { this.selectedVariants = selectedVariants; }
}