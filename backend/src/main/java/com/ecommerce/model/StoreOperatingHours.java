package com.ecommerce.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreOperatingHours {
    private Long id;
    private Long storeId;
    private Integer dayOfWeek; // 0=Sunday, 1=Monday, ..., 6=Saturday
    private LocalTime openTime;
    private LocalTime closeTime;
    private Boolean isClosed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Helper method to get day name
    public String getDayName() {
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return dayOfWeek >= 0 && dayOfWeek <= 6 ? days[dayOfWeek] : "Unknown";
    }
}