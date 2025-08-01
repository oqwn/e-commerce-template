package com.ecommerce.dto;

import com.ecommerce.model.Product;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequest {
    
    private Long categoryId;
    
    @Size(min = 3, max = 255, message = "Product name must be between 3 and 255 characters")
    private String name;
    
    private String description;
    
    @Size(max = 500, message = "Short description must not exceed 500 characters")
    private String shortDescription;
    
    @Size(max = 100, message = "SKU must not exceed 100 characters")
    private String sku;
    
    @DecimalMin(value = "0.00", message = "Price must be greater than or equal to 0")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 integer digits and 2 fraction digits")
    private BigDecimal price;
    
    @DecimalMin(value = "0.00", message = "Compare at price must be greater than or equal to 0")
    @Digits(integer = 8, fraction = 2, message = "Compare at price must have at most 8 integer digits and 2 fraction digits")
    private BigDecimal compareAtPrice;
    
    @DecimalMin(value = "0.00", message = "Cost must be greater than or equal to 0")
    @Digits(integer = 8, fraction = 2, message = "Cost must have at most 8 integer digits and 2 fraction digits")
    private BigDecimal cost;
    
    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    private Integer quantity;
    
    private Boolean trackQuantity;
    
    @DecimalMin(value = "0.000", message = "Weight must be greater than or equal to 0")
    @Digits(integer = 7, fraction = 3, message = "Weight must have at most 7 integer digits and 3 fraction digits")
    private BigDecimal weight;
    
    @Pattern(regexp = "^(kg|g|lb|oz)$", message = "Weight unit must be one of: kg, g, lb, oz")
    private String weightUnit;
    
    private Product.ProductStatus status;
    
    private Boolean featured;
    
    private List<CreateProductRequest.ProductImageRequest> images;
    
    private List<String> tags;
    
    private Map<String, String> attributes;
}