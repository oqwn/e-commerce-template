package com.ecommerce.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
public class CreateFlashSaleRequest {
    
    @NotBlank(message = "Flash sale name is required")
    @Size(max = 255, message = "Flash sale name cannot exceed 255 characters")
    private String name;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;
    
    @NotNull(message = "End time is required")
    private LocalDateTime endTime;
    
    @DecimalMin(value = "0.01", message = "Discount percentage must be at least 0.01%")
    @DecimalMax(value = "100.00", message = "Discount percentage cannot exceed 100%")
    private BigDecimal discountPercentage;
    
    @Min(value = 1, message = "Max quantity must be at least 1")
    private Integer maxQuantity;
    
    @NotEmpty(message = "At least one product must be included")
    @Valid
    private List<FlashSaleProductRequest> products;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlashSaleProductRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;
        
        @NotNull(message = "Original price is required")
        @DecimalMin(value = "0.00", message = "Original price must be non-negative")
        private BigDecimal originalPrice;
        
        @NotNull(message = "Sale price is required")
        @DecimalMin(value = "0.00", message = "Sale price must be non-negative")
        private BigDecimal salePrice;
        
        @Min(value = 1, message = "Max quantity per product must be at least 1")
        private Integer maxQuantityPerProduct;
    }
}