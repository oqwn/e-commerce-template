package com.ecommerce.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Coupon {
    private Long id;
    private String code;
    private String name;
    private String description;
    private CouponType type;
    private BigDecimal value;
    private BigDecimal minimumOrderAmount;
    private BigDecimal maximumDiscountAmount;
    private Integer usageLimit;
    private Integer usedCount;
    private Integer perUserLimit;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // For joined data
    private List<CouponUsage> usages;
    
    public enum CouponType {
        PERCENTAGE, FIXED_AMOUNT, FREE_SHIPPING
    }
    
    // Constructors
    public Coupon() {}
    
    public Coupon(String code, String name, CouponType type, BigDecimal value, 
                  LocalDateTime validFrom, LocalDateTime validUntil) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.value = value;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.minimumOrderAmount = BigDecimal.ZERO;
        this.usedCount = 0;
        this.perUserLimit = 1;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public CouponType getType() { return type; }
    public void setType(CouponType type) { this.type = type; }
    
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
    
    public BigDecimal getMinimumOrderAmount() { return minimumOrderAmount; }
    public void setMinimumOrderAmount(BigDecimal minimumOrderAmount) { this.minimumOrderAmount = minimumOrderAmount; }
    
    public BigDecimal getMaximumDiscountAmount() { return maximumDiscountAmount; }
    public void setMaximumDiscountAmount(BigDecimal maximumDiscountAmount) { this.maximumDiscountAmount = maximumDiscountAmount; }
    
    public Integer getUsageLimit() { return usageLimit; }
    public void setUsageLimit(Integer usageLimit) { this.usageLimit = usageLimit; }
    
    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }
    
    public Integer getPerUserLimit() { return perUserLimit; }
    public void setPerUserLimit(Integer perUserLimit) { this.perUserLimit = perUserLimit; }
    
    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }
    
    public LocalDateTime getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDateTime validUntil) { this.validUntil = validUntil; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<CouponUsage> getUsages() { return usages; }
    public void setUsages(List<CouponUsage> usages) { this.usages = usages; }
    
    // Utility methods
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && 
               (validFrom == null || !now.isBefore(validFrom)) && 
               (validUntil == null || !now.isAfter(validUntil));
    }
    
    public boolean isExpired() {
        return validUntil != null && LocalDateTime.now().isAfter(validUntil);
    }
    
    public boolean hasUsageLeft() {
        return usageLimit == null || usedCount < usageLimit;
    }
    
    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if (!isValid() || !hasUsageLeft()) {
            return BigDecimal.ZERO;
        }
        
        if (minimumOrderAmount != null && orderAmount.compareTo(minimumOrderAmount) < 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discount = BigDecimal.ZERO;
        
        switch (type) {
            case PERCENTAGE:
                discount = orderAmount.multiply(value.divide(BigDecimal.valueOf(100)));
                break;
            case FIXED_AMOUNT:
                discount = value;
                break;
            case FREE_SHIPPING:
                // This would be handled in shipping calculation
                return BigDecimal.ZERO;
        }
        
        // Apply maximum discount limit if set
        if (maximumDiscountAmount != null && discount.compareTo(maximumDiscountAmount) > 0) {
            discount = maximumDiscountAmount;
        }
        
        // Discount cannot exceed order amount
        if (discount.compareTo(orderAmount) > 0) {
            discount = orderAmount;
        }
        
        return discount;
    }
    
    public void incrementUsage() {
        this.usedCount++;
        this.updatedAt = LocalDateTime.now();
    }
}