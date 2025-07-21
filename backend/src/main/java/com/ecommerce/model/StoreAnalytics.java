package com.ecommerce.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreAnalytics {
    private Long id;
    private Long storeId;
    private LocalDate date;
    
    // Traffic Metrics
    private Integer totalVisits;
    private Integer uniqueVisitors;
    private Integer pageViews;
    private BigDecimal bounceRate;
    private Integer avgSessionDuration; // in seconds
    
    // Sales Metrics
    private Integer totalOrders;
    private BigDecimal totalRevenue;
    private BigDecimal avgOrderValue;
    private BigDecimal conversionRate;
    
    // Product Metrics
    private Integer productsViewed;
    private Integer productsAddedToCart;
    private Integer productsPurchased;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}