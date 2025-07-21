package com.ecommerce.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreCustomization {
    private Long id;
    private Long storeId;
    
    // Theme Settings
    private String themeName;
    private String primaryColor;
    private String secondaryColor;
    private String accentColor;
    private String backgroundColor;
    private String textColor;
    
    // Layout Settings
    private LayoutType layoutType;
    private Integer productsPerPage;
    private Boolean showBanner;
    private Boolean showFeaturedProducts;
    private Boolean showCategories;
    
    // Custom CSS
    private String customCss;
    
    // SEO Settings
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum LayoutType {
        GRID,
        LIST,
        MASONRY
    }
}