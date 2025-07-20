package com.ecommerce.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ProductVariant {
    private Long id;
    private Long productId;
    private String name;
    private String sku;
    private BigDecimal price;
    private BigDecimal compareAtPrice;
    private Integer quantity;
    private String imageUrl;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Variant options (e.g., {color: "Black", size: "M"})
    private Map<String, String> options;
}