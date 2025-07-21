package com.ecommerce.dto;

import java.math.BigDecimal;

public class ShippingMethodResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer estimatedDaysMin;
    private Integer estimatedDaysMax;
    private String estimatedDeliveryText;
    private Boolean isFree;
    private Boolean isExpressDelivery;
    
    // Constructors
    public ShippingMethodResponse() {}
    
    public ShippingMethodResponse(Long id, String name, String description, BigDecimal price,
                                 Integer estimatedDaysMin, Integer estimatedDaysMax) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.estimatedDaysMin = estimatedDaysMin;
        this.estimatedDaysMax = estimatedDaysMax;
        this.isFree = price.compareTo(BigDecimal.ZERO) == 0;
        this.isExpressDelivery = estimatedDaysMax <= 2;
        this.estimatedDeliveryText = calculateEstimatedDeliveryText();
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
    
    public String getEstimatedDeliveryText() { return estimatedDeliveryText; }
    public void setEstimatedDeliveryText(String estimatedDeliveryText) { this.estimatedDeliveryText = estimatedDeliveryText; }
    
    public Boolean getIsFree() { return isFree; }
    public void setIsFree(Boolean isFree) { this.isFree = isFree; }
    
    public Boolean getIsExpressDelivery() { return isExpressDelivery; }
    public void setIsExpressDelivery(Boolean isExpressDelivery) { this.isExpressDelivery = isExpressDelivery; }
    
    // Utility methods
    private String calculateEstimatedDeliveryText() {
        if (estimatedDaysMin != null && estimatedDaysMax != null) {
            if (estimatedDaysMin.equals(estimatedDaysMax)) {
                return estimatedDaysMin + " day" + (estimatedDaysMin > 1 ? "s" : "");
            } else {
                return estimatedDaysMin + "-" + estimatedDaysMax + " days";
            }
        }
        return "";
    }
}