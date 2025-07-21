package com.ecommerce.dto;

import lombok.Data;
import java.time.LocalTime;

@Data
public class UpdateOperatingHoursRequest {
    private Integer dayOfWeek; // 0=Sunday, 1=Monday, ..., 6=Saturday
    private LocalTime openTime;
    private LocalTime closeTime;
    private Boolean isClosed;
}