package com.ecommerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class CartItemResponse {
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
    private Boolean inStock;
    private Integer availableQuantity;
    private LocalDateTime addedAt;
    
    // Constructors
    public CartItemResponse() {}
    
    public CartItemResponse(Long id, Long productId, String productName, String productSlug,
                           String productImageUrl, String storeName, Long storeId, Integer quantity,
                           BigDecimal priceAtTime, BigDecimal totalPrice, Map<String, Object> selectedVariants,
                           Boolean inStock, Integer availableQuantity, LocalDateTime addedAt) {
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
        this.selectedVariants = selectedVariants;
        this.inStock = inStock;
        this.availableQuantity = availableQuantity;
        this.addedAt = addedAt;
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
    
    public Boolean getInStock() { return inStock; }
    public void setInStock(Boolean inStock) { this.inStock = inStock; }
    
    public Integer getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(Integer availableQuantity) { this.availableQuantity = availableQuantity; }
    
    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}