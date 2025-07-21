package com.ecommerce.dto;

import java.math.BigDecimal;
import java.util.Map;

public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productSlug;
    private String productImageUrl;
    private String storeName;
    private Long storeId;
    private Integer quantity;
    private BigDecimal priceAtTime;
    private BigDecimal totalPrice;
    private Map<String, Object> selectedVariants;
    
    // Constructors
    public OrderItemResponse() {}
    
    public OrderItemResponse(Long id, Long productId, String productName, String productSlug,
                            String productImageUrl, String storeName, Long storeId, Integer quantity,
                            BigDecimal priceAtTime, BigDecimal totalPrice) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productSlug = productSlug;
        this.productImageUrl = productImageUrl;
        this.storeName = storeName;
        this.storeId = storeId;
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
        this.totalPrice = totalPrice;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getProductSlug() { return productSlug; }
    public void setProductSlug(String productSlug) { this.productSlug = productSlug; }
    
    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }
    
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    
    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getPriceAtTime() { return priceAtTime; }
    public void setPriceAtTime(BigDecimal priceAtTime) { this.priceAtTime = priceAtTime; }
    
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    
    public Map<String, Object> getSelectedVariants() { return selectedVariants; }
    public void setSelectedVariants(Map<String, Object> selectedVariants) { this.selectedVariants = selectedVariants; }
}