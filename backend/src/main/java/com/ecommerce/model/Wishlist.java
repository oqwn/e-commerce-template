package com.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wishlist {
    private Long id;
    private Long userId;
    private Long productId;
    private String notes;
    @Builder.Default
    private Integer priority = 0;
    private LocalDateTime createdAt;
    
    // Additional fields for joined data
    private String userFirstName;
    private String userLastName;
    private String productName;
    private String productDescription;
    private String productPrice;
    private String productImage;
    private String sellerName;
}