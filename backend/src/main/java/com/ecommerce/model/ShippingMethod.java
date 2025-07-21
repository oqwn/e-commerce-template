package com.ecommerce.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ShippingMethod {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer estimatedDaysMin;
    private Integer estimatedDaysMax;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public ShippingMethod() {}
    
    public ShippingMethod(String name, String description, BigDecimal price, 
                         Integer estimatedDaysMin, Integer estimatedDaysMax) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.estimatedDaysMin = estimatedDaysMin;
        this.estimatedDaysMax = estimatedDaysMax;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Integer getEstimatedDaysMin() { return estimatedDaysMin; }
    public void setEstimatedDaysMin(Integer estimatedDaysMin) { this.estimatedDaysMin = estimatedDaysMin; }
    
    public Integer getEstimatedDaysMax() { return estimatedDaysMax; }
    public void setEstimatedDaysMax(Integer estimatedDaysMax) { this.estimatedDaysMax = estimatedDaysMax; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Utility methods
    public String getEstimatedDeliveryText() {
        if (estimatedDaysMin.equals(estimatedDaysMax)) {
            return estimatedDaysMin + " day" + (estimatedDaysMin > 1 ? "s" : "");
        } else {
            return estimatedDaysMin + "-" + estimatedDaysMax + " days";
        }
    }
    
    public boolean isFree() {
        return price.compareTo(BigDecimal.ZERO) == 0;
    }
    
    public boolean isExpressDelivery() {
        return estimatedDaysMax <= 2;
    }
}