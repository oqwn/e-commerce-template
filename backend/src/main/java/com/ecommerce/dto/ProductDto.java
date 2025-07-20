package com.ecommerce.dto;

import com.ecommerce.model.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long id;
    private Long sellerId;
    private String sellerName;
    private Long categoryId;
    private String categoryName;
    private String categorySlug;
    private String name;
    private String slug;
    private String description;
    private String shortDescription;
    private String sku;
    private BigDecimal price;
    private BigDecimal compareAtPrice;
    private Integer quantity;
    private Boolean trackQuantity;
    private BigDecimal weight;
    private String weightUnit;
    private String status;
    private Boolean featured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    
    // Related data
    private List<ProductImageDto> images;
    private List<String> tags;
    private Map<String, String> attributes;
    private List<ProductVariantDto> variants;
    
    // Stats
    private Double averageRating;
    private Integer reviewCount;
    private Integer viewCount;
    
    // Computed fields
    private Boolean inStock;
    private BigDecimal discountPercentage;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImageDto {
        private Long id;
        private String imageUrl;
        private String altText;
        private Integer displayOrder;
        private Boolean isPrimary;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductVariantDto {
        private Long id;
        private String name;
        private String sku;
        private BigDecimal price;
        private BigDecimal compareAtPrice;
        private Integer quantity;
        private String imageUrl;
        private Map<String, String> options;
    }
    
    public static ProductDto fromProduct(Product product) {
        ProductDto dto = ProductDto.builder()
                .id(product.getId())
                .sellerId(product.getSellerId())
                .categoryId(product.getCategoryId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .shortDescription(product.getShortDescription())
                .sku(product.getSku())
                .price(product.getPrice())
                .compareAtPrice(product.getCompareAtPrice())
                .quantity(product.getQuantity())
                .trackQuantity(product.getTrackQuantity())
                .weight(product.getWeight())
                .weightUnit(product.getWeightUnit())
                .status(product.getStatus() != null ? product.getStatus().name() : null)
                .featured(product.getFeatured())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .publishedAt(product.getPublishedAt())
                .averageRating(product.getAverageRating())
                .reviewCount(product.getReviewCount())
                .viewCount(product.getViewCount())
                .build();
        
        // Set seller name
        if (product.getSeller() != null) {
            dto.setSellerName(product.getSeller().getFirstName() + " " + product.getSeller().getLastName());
        }
        
        // Set category info
        if (product.getCategory() != null) {
            dto.setCategoryName(product.getCategory().getName());
            dto.setCategorySlug(product.getCategory().getSlug());
        }
        
        // Convert images
        if (product.getImages() != null) {
            dto.setImages(product.getImages().stream()
                    .map(img -> ProductImageDto.builder()
                            .id(img.getId())
                            .imageUrl(img.getImageUrl())
                            .altText(img.getAltText())
                            .displayOrder(img.getDisplayOrder())
                            .isPrimary(img.getIsPrimary())
                            .build())
                    .collect(Collectors.toList()));
        }
        
        // Set tags
        dto.setTags(product.getTags());
        
        // Convert attributes
        if (product.getAttributes() != null) {
            dto.setAttributes(product.getAttributes().stream()
                    .collect(Collectors.toMap(
                            ProductAttribute::getAttributeName,
                            ProductAttribute::getAttributeValue
                    )));
        }
        
        // Convert variants
        if (product.getVariants() != null) {
            dto.setVariants(product.getVariants().stream()
                    .map(variant -> ProductVariantDto.builder()
                            .id(variant.getId())
                            .name(variant.getName())
                            .sku(variant.getSku())
                            .price(variant.getPrice())
                            .compareAtPrice(variant.getCompareAtPrice())
                            .quantity(variant.getQuantity())
                            .imageUrl(variant.getImageUrl())
                            .options(variant.getOptions())
                            .build())
                    .collect(Collectors.toList()));
        }
        
        // Calculate computed fields
        dto.setInStock(product.getQuantity() != null && product.getQuantity() > 0);
        
        if (product.getPrice() != null && product.getCompareAtPrice() != null 
                && product.getCompareAtPrice().compareTo(product.getPrice()) > 0) {
            BigDecimal discount = product.getCompareAtPrice().subtract(product.getPrice());
            dto.setDiscountPercentage(discount.divide(product.getCompareAtPrice(), 2, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100)));
        }
        
        return dto;
    }
}