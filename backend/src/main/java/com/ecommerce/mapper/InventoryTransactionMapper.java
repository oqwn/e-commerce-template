package com.ecommerce.mapper;

import com.ecommerce.model.InventoryTransaction;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InventoryTransactionMapper {
    
    @Insert("INSERT INTO inventory_transactions (product_id, variant_id, transaction_type, " +
            "quantity, reference_type, reference_id, notes, created_by) " +
            "VALUES (#{productId}, #{variantId}, #{transactionType}, #{quantity}, " +
            "#{referenceType}, #{referenceId}, #{notes}, #{createdBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(InventoryTransaction transaction);
    
    @Select("SELECT * FROM inventory_transactions WHERE product_id = #{productId} " +
            "ORDER BY created_at DESC")
    @Results({
        @Result(property = "productId", column = "product_id"),
        @Result(property = "variantId", column = "variant_id"),
        @Result(property = "transactionType", column = "transaction_type"),
        @Result(property = "referenceType", column = "reference_type"),
        @Result(property = "referenceId", column = "reference_id"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "createdAt", column = "created_at")
    })
    List<InventoryTransaction> findByProductId(Long productId);
    
    @Select("SELECT * FROM inventory_transactions WHERE variant_id = #{variantId} " +
            "ORDER BY created_at DESC")
    List<InventoryTransaction> findByVariantId(Long variantId);
    
    @Select("SELECT * FROM inventory_transactions WHERE reference_type = #{referenceType} " +
            "AND reference_id = #{referenceId}")
    List<InventoryTransaction> findByReference(@Param("referenceType") String referenceType,
                                             @Param("referenceId") Long referenceId);
}