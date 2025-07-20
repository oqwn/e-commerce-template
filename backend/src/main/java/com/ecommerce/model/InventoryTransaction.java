package com.ecommerce.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class InventoryTransaction {
    private Long id;
    private Long productId;
    private Long variantId;
    private String transactionType; // STOCK_IN, STOCK_OUT, ADJUSTMENT, RETURN, DAMAGE
    private Integer quantity;
    private String referenceType; // ORDER, MANUAL, RETURN, INITIAL, etc.
    private Long referenceId;
    private String notes;
    private Long createdBy;
    private LocalDateTime createdAt;
}