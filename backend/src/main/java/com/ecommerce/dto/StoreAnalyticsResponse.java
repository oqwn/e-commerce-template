package com.ecommerce.dto;

import com.ecommerce.model.StoreAnalytics;
import lombok.Data;

import java.util.List;

@Data
public class StoreAnalyticsResponse {
    private List<StoreAnalytics> analytics;
    private AnalyticsSummaryResponse summary;
}