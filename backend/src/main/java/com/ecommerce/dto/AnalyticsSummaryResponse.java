package com.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AnalyticsSummaryResponse {
    private BigDecimal totalRevenue;
    private Integer totalOrders;
    private BigDecimal avgOrderValue;
    private Double conversionRate;
    private Double revenueChange;
    private Double ordersChange;
    private Double conversionChange;
}