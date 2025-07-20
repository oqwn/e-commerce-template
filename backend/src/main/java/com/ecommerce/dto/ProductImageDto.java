package com.ecommerce.dto;

import com.ecommerce.model.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageDto {
    private Long id;
    private Long productId;
    private String imageUrl;
    private String altText;
    private Integer displayOrder;
    private Boolean isPrimary;
    private LocalDateTime createdAt;

    public static ProductImageDto fromProductImage(ProductImage productImage) {
        if (productImage == null) {
            return null;
        }
        
        return ProductImageDto.builder()
                .id(productImage.getId())
                .productId(productImage.getProductId())
                .imageUrl(productImage.getImageUrl())
                .altText(productImage.getAltText())
                .displayOrder(productImage.getDisplayOrder())
                .isPrimary(productImage.getIsPrimary())
                .createdAt(productImage.getCreatedAt())
                .build();
    }
}