package com.ecommerce.dto.response;

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
public class FlashSaleResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal discountPercentage;
    private Integer maxQuantity;
    private Integer usedQuantity;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private String creatorName;
    
    // Status information
    private boolean currentlyActive;
    private boolean upcoming;
    private boolean expired;
    private boolean hasQuantityAvailable;
    private int remainingQuantity;
    
    // Associated products
    private List<FlashSaleProductResponse> products;
}