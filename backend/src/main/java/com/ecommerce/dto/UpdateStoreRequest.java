package com.ecommerce.dto;

import lombok.Data;

@Data
public class UpdateStoreRequest {
    private String storeName;
    private String description;
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
}