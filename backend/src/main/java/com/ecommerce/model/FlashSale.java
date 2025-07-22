package com.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSale {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal discountPercentage;
    private Integer maxQuantity;
    @Builder.Default
    private Integer usedQuantity = 0;
    @Builder.Default
    private Boolean isActive = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    
    // Joined data
    private String creatorName;
    private List<FlashSaleProduct> products;
    
    // Computed properties
    public boolean isCurrentlyActive() {
        if (!isActive) return false;
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startTime) && now.isBefore(endTime);
    }
    
    public boolean isUpcoming() {
        return isActive && LocalDateTime.now().isBefore(startTime);
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(endTime);
    }
    
    public boolean hasQuantityAvailable() {
        if (maxQuantity == null) return true;
        return usedQuantity < maxQuantity;
    }
    
    public int getRemainingQuantity() {
        if (maxQuantity == null) return Integer.MAX_VALUE;
        return Math.max(0, maxQuantity - usedQuantity);
    }
}