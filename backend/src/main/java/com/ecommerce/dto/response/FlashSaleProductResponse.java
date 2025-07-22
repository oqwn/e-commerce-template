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
public class FlashSaleProductResponse {
    private Long id;
    private Long flashSaleId;
    private Long productId;
    private BigDecimal originalPrice;
    private BigDecimal salePrice;
    private Integer maxQuantityPerProduct;
    private Integer usedQuantityPerProduct;
    private LocalDateTime createdAt;
    
    // Product details
    private String productName;
    private String productSlug;
    private String productImage;
    private String sellerName;
    
    // Flash sale details
    private String flashSaleName;
    private LocalDateTime flashSaleStartTime;
    private LocalDateTime flashSaleEndTime;
    
    // Computed values
    private boolean hasQuantityAvailable;
    private int remainingQuantity;
    private BigDecimal discountAmount;
    private BigDecimal discountPercentage;
}