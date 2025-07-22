package com.ecommerce.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistRequest {
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
    
    @Min(value = 0, message = "Priority must be at least 0")
    @Max(value = 10, message = "Priority cannot exceed 10")
    private Integer priority;
}