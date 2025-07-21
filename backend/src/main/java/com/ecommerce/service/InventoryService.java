package com.ecommerce.service;

import com.ecommerce.mapper.InventoryTransactionMapper;
import com.ecommerce.model.InventoryTransaction;
import com.ecommerce.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {
    
    private final InventoryTransactionMapper inventoryTransactionMapper;
    
    @Transactional
    public void addInventoryTransaction(Long productId, Long variantId, String transactionType, 
                                      Integer quantity, String referenceType, Long referenceId, 
                                      String notes) {
        Long userId = SecurityUtils.getCurrentUserId();
        
        InventoryTransaction transaction = InventoryTransaction.builder()
                .productId(productId)
                .variantId(variantId)
                .transactionType(transactionType)
                .quantity(quantity)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .notes(notes)
                .createdBy(userId)
                .build();
        
        inventoryTransactionMapper.insert(transaction);
        log.info("Inventory transaction recorded: {} {} units for product {}", 
                transactionType, quantity, productId);
    }
    
    public List<InventoryTransaction> getProductTransactions(Long productId) {
        return inventoryTransactionMapper.findByProductId(productId);
    }
    
    public List<InventoryTransaction> getVariantTransactions(Long variantId) {
        return inventoryTransactionMapper.findByVariantId(variantId);
    }
    
    public Integer calculateCurrentStock(Long productId, Long variantId) {
        List<InventoryTransaction> transactions;
        if (variantId != null) {
            transactions = inventoryTransactionMapper.findByVariantId(variantId);
        } else {
            transactions = inventoryTransactionMapper.findByProductId(productId);
        }
        
        int stock = 0;
        for (InventoryTransaction transaction : transactions) {
            switch (transaction.getTransactionType()) {
                case "STOCK_IN":
                case "RETURN":
                    stock += transaction.getQuantity();
                    break;
                case "STOCK_OUT":
                case "DAMAGE":
                    stock -= transaction.getQuantity();
                    break;
                case "ADJUSTMENT":
                    // Adjustment can be positive or negative
                    stock += transaction.getQuantity();
                    break;
            }
        }
        
        return stock;
    }
    
    @Transactional
    public void reserveInventory(Long productId, Integer quantity, String notes) {
        addInventoryTransaction(productId, null, "RESERVE", -quantity, "ORDER", null, notes);
        log.info("Reserved {} units for product {}: {}", quantity, productId, notes);
    }
    
    @Transactional
    public void releaseReservedInventory(Long productId, Integer quantity, String notes) {
        addInventoryTransaction(productId, null, "RELEASE", quantity, "ORDER", null, notes);
        log.info("Released {} reserved units for product {}: {}", quantity, productId, notes);
    }
    
    @Transactional
    public void commitReservedInventory(Long productId, Integer quantity, String notes) {
        addInventoryTransaction(productId, null, "COMMIT", -quantity, "ORDER", null, notes);
        log.info("Committed {} reserved units for product {}: {}", quantity, productId, notes);
    }
}