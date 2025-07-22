package com.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productDescription;
    private BigDecimal productPrice;
    private String productImage;
    private String sellerName;
    private String notes;
    private Integer priority;
    private LocalDateTime createdAt;
}