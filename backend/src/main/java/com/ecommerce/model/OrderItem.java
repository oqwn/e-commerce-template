package com.ecommerce.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private Long storeId;
    private Integer quantity;
    private BigDecimal priceAtTime;
    private BigDecimal totalPrice;
    private Map<String, Object> selectedVariants;
    private String productName;
    private String productImageUrl;
    private LocalDateTime createdAt;
    
    // For joined data
    private Order order;
    private Product product;
    private Store store;
    
    // Constructors
    public OrderItem() {}
    
    public OrderItem(Long orderId, Long productId, Long storeId, Integer quantity, 
                     BigDecimal priceAtTime, String productName) {
        this.orderId = orderId;
        this.productId = productId;
        this.storeId = storeId;
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
        this.totalPrice = priceAtTime.multiply(BigDecimal.valueOf(quantity));
        this.productName = productName;
        this.createdAt = LocalDateTime.now();
    }
    
    public OrderItem(Long orderId, Long productId, Long storeId, Integer quantity, 
                     BigDecimal priceAtTime, String productName, Map<String, Object> selectedVariants) {
        this(orderId, productId, storeId, quantity, priceAtTime, productName);
        this.selectedVariants = selectedVariants;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity;
        this.totalPrice = priceAtTime.multiply(BigDecimal.valueOf(quantity));
    }
    
    public BigDecimal getPriceAtTime() { return priceAtTime; }
    public void setPriceAtTime(BigDecimal priceAtTime) { 
        this.priceAtTime = priceAtTime;
        this.totalPrice = priceAtTime.multiply(BigDecimal.valueOf(quantity));
    }
    
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    
    public Map<String, Object> getSelectedVariants() { return selectedVariants; }
    public void setSelectedVariants(Map<String, Object> selectedVariants) { this.selectedVariants = selectedVariants; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public Store getStore() { return store; }
    public void setStore(Store store) { this.store = store; }
    
    // Utility methods
    public boolean hasVariants() {
        return selectedVariants != null && !selectedVariants.isEmpty();
    }
    
    public void recalculateTotalPrice() {
        this.totalPrice = priceAtTime.multiply(BigDecimal.valueOf(quantity));
    }
}