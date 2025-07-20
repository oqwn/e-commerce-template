package com.ecommerce.mapper;

import com.ecommerce.model.ProductVariant;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductVariantMapper {
    
    @Select("SELECT * FROM product_variants WHERE id = #{id}")
    @Results(id = "variantResultMap", value = {
        @Result(property = "productId", column = "product_id"),
        @Result(property = "compareAtPrice", column = "compare_at_price"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "displayOrder", column = "display_order"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    ProductVariant findById(Long id);
    
    @Select("SELECT * FROM product_variants WHERE product_id = #{productId} ORDER BY display_order")
    @ResultMap("variantResultMap")
    List<ProductVariant> findByProductId(Long productId);
    
    @Insert("INSERT INTO product_variants (product_id, name, sku, price, compare_at_price, " +
            "quantity, image_url, display_order) VALUES (#{productId}, #{name}, #{sku}, " +
            "#{price}, #{compareAtPrice}, #{quantity}, #{imageUrl}, #{displayOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ProductVariant variant);
    
    @Update("UPDATE product_variants SET name = #{name}, sku = #{sku}, price = #{price}, " +
            "compare_at_price = #{compareAtPrice}, quantity = #{quantity}, " +
            "image_url = #{imageUrl}, display_order = #{displayOrder} WHERE id = #{id}")
    void update(ProductVariant variant);
    
    @Delete("DELETE FROM product_variants WHERE id = #{id}")
    void delete(Long id);
    
    @Delete("DELETE FROM product_variants WHERE product_id = #{productId}")
    void deleteByProductId(Long productId);
    
    @Update("UPDATE product_variants SET quantity = quantity + #{quantity} WHERE id = #{variantId}")
    void updateQuantity(@Param("variantId") Long variantId, @Param("quantity") Integer quantity);
}