package com.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSearchRequest {
    
    private String keyword;
    
    private Long categoryId;
    
    private Long sellerId;
    
    @DecimalMin(value = "0.00", message = "Min price must be greater than or equal to 0")
    private BigDecimal minPrice;
    
    @DecimalMin(value = "0.00", message = "Max price must be greater than or equal to 0")
    private BigDecimal maxPrice;
    
    private Boolean featured;
    
    private List<String> tags;
    
    @Pattern(regexp = "^(price_asc|price_desc|name|newest|rating)$", 
            message = "Sort by must be one of: price_asc, price_desc, name, newest, rating")
    private String sortBy = "newest";
    
    @Min(value = 1, message = "Limit must be at least 1")
    @Max(value = 100, message = "Limit must not exceed 100")
    private Integer limit = 20;
    
    @Min(value = 0, message = "Offset must be at least 0")
    private Integer offset = 0;
}