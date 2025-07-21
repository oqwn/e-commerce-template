package com.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class CreateStoreRequest {
    @NotBlank(message = "Store name is required")
    private String storeName;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotBlank(message = "Business name is required")
    private String businessName;
    
    @NotBlank(message = "Contact email is required")
    @Email(message = "Invalid email format")
    private String contactEmail;
    
    @NotBlank(message = "Contact phone is required")
    private String contactPhone;
    
    // Address
    @NotBlank(message = "Street address is required")
    private String street;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "State is required")
    private String state;
    
    @NotBlank(message = "Postal code is required")
    private String postalCode;
    
    private String country = "US";
}