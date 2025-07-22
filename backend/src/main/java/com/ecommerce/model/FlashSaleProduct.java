package com.ecommerce.model;

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
public class FlashSaleProduct {
    private Long id;
    private Long flashSaleId;
    private Long productId;
    private BigDecimal originalPrice;
    private BigDecimal salePrice;
    private Integer maxQuantityPerProduct;
    @Builder.Default
    private Integer usedQuantityPerProduct = 0;
    private LocalDateTime createdAt;
    
    // Joined data
    private String productName;
    private String productSlug;
    private String productImage;
    private String sellerName;
    
    // Computed properties
    public boolean hasQuantityAvailable() {
        if (maxQuantityPerProduct == null) return true;
        return usedQuantityPerProduct < maxQuantityPerProduct;
    }
    
    public int getRemainingQuantity() {
        if (maxQuantityPerProduct == null) return Integer.MAX_VALUE;
        return Math.max(0, maxQuantityPerProduct - usedQuantityPerProduct);
    }
    
    public BigDecimal getDiscountAmount() {
        return originalPrice.subtract(salePrice);
    }
    
    public BigDecimal getDiscountPercentage() {
        if (originalPrice.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return getDiscountAmount()
                .divide(originalPrice, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }
}