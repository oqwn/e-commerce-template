package com.ecommerce.model;

import lombok.Data;
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
public class Store {
    private Long id;
    private Long sellerId;
    private String storeName;
    private String storeSlug;
    private String description;
    private String logoUrl;
    private String bannerUrl;
    
    // Business Information
    private String businessName;
    private String businessRegistrationNumber;
    private String taxId;
    
    // Contact Information
    private String contactEmail;
    private String contactPhone;
    private String supportEmail;
    private String supportPhone;
    
    // Address
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    
    // Store Settings
    private String currency;
    private String timezone;
    private Boolean isActive;
    private Boolean isVerified;
    private VerificationStatus verificationStatus;
    private LocalDateTime verificationDate;
    
    // Store Policies
    private String returnPolicy;
    private String shippingPolicy;
    private String privacyPolicy;
    private String termsAndConditions;
    
    // Social Media
    private String websiteUrl;
    private String facebookUrl;
    private String instagramUrl;
    private String twitterUrl;
    private String youtubeUrl;
    
    // Store Metrics
    private BigDecimal rating;
    private Integer totalReviews;
    private Integer totalProducts;
    private Integer totalSales;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Related entities
    private User seller;
    private List<Category> categories;
    private StoreCustomization customization;
    private List<StoreOperatingHours> operatingHours;
    
    public enum VerificationStatus {
        PENDING,
        IN_REVIEW,
        APPROVED,
        REJECTED,
        SUSPENDED
    }
}