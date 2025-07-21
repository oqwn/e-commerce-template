package com.ecommerce.dto;

import com.ecommerce.model.StoreCustomization;
import lombok.Data;

@Data
public class UpdateStoreCustomizationRequest {
    private String themeName;
    private String primaryColor;
    private String secondaryColor;
    private String accentColor;
    private String backgroundColor;
    private String textColor;
    private StoreCustomization.LayoutType layoutType;
    private Integer productsPerPage;
    private Boolean showBanner;
    private Boolean showFeaturedProducts;
    private Boolean showCategories;
    private String customCss;
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
}