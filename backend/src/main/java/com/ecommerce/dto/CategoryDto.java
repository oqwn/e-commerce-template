package com.ecommerce.dto;

import com.ecommerce.model.Category;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private Long parentId;
    private String parentName;
    private String imageUrl;
    private Boolean isActive;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields
    private Integer productCount;
    private List<CategoryDto> children;
    
    public static CategoryDto fromCategory(Category category) {
        CategoryDto dto = CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .parentId(category.getParentId())
                .imageUrl(category.getImageUrl())
                .isActive(category.getIsActive())
                .displayOrder(category.getDisplayOrder())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .productCount(category.getProductCount())
                .build();
        
        if (category.getParent() != null) {
            dto.setParentName(category.getParent().getName());
        }
        
        if (category.getChildren() != null) {
            dto.setChildren(category.getChildren().stream()
                    .map(CategoryDto::fromCategory)
                    .toList());
        }
        
        return dto;
    }
}