package com.ecommerce.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Product {
    private Long id;
    private Long sellerId;
    private Long categoryId;
    private String name;
    private String slug;
    private String description;
    private String shortDescription;
    private String sku;
    private BigDecimal price;
    private BigDecimal compareAtPrice;
    private BigDecimal cost;
    private Integer quantity;
    private Boolean trackQuantity;
    private BigDecimal weight;
    private String weightUnit;
    private ProductStatus status;
    private Boolean featured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    
    // Related entities
    private User seller;
    private Category category;
    private List<ProductImage> images;
    private List<String> tags;
    private List<ProductAttribute> attributes;
    private List<ProductVariant> variants;
    
    // Computed fields
    private Double averageRating;
    private Integer reviewCount;
    private Integer viewCount;
    
    public enum ProductStatus {
        DRAFT,
        ACTIVE,
        INACTIVE,
        OUT_OF_STOCK
    }
}