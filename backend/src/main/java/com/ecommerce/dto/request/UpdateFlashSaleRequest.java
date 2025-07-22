package com.ecommerce.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFlashSaleRequest {
    
    @Size(max = 255, message = "Flash sale name cannot exceed 255 characters")
    private String name;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    @DecimalMin(value = "0.01", message = "Discount percentage must be at least 0.01%")
    @DecimalMax(value = "100.00", message = "Discount percentage cannot exceed 100%")
    private BigDecimal discountPercentage;
    
    @Min(value = 1, message = "Max quantity must be at least 1")
    private Integer maxQuantity;
    
    private Boolean isActive;
}