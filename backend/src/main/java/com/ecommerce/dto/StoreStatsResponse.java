package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreStatsResponse {
    private Long totalStores;
    private Long activeStores;
    private Long verifiedStores;
    private Long pendingVerification;
}