package com.ecommerce.mapper;

import com.ecommerce.model.ProductImage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductImageMapper {
    
    @Insert("INSERT INTO product_images (product_id, image_url, alt_text, display_order, is_primary) " +
            "VALUES (#{productId}, #{imageUrl}, #{altText}, #{displayOrder}, #{isPrimary})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ProductImage image);
    
    @Select("SELECT * FROM product_images WHERE product_id = #{productId} ORDER BY display_order")
    @Results({
        @Result(property = "productId", column = "product_id"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "altText", column = "alt_text"),
        @Result(property = "displayOrder", column = "display_order"),
        @Result(property = "isPrimary", column = "is_primary"),
        @Result(property = "createdAt", column = "created_at")
    })
    List<ProductImage> findByProductId(Long productId);
    
    @Select("SELECT * FROM product_images WHERE product_id = #{productId} AND is_primary = 1 LIMIT 1")
    ProductImage findPrimaryByProductId(Long productId);
    
    @Delete("DELETE FROM product_images WHERE product_id = #{productId}")
    void deleteByProductId(Long productId);
    
    @Delete("DELETE FROM product_images WHERE id = #{id}")
    void deleteById(Long id);
    
    @Update("UPDATE product_images SET is_primary = 0 WHERE product_id = #{productId}")
    void clearPrimaryFlag(Long productId);
    
    @Update("UPDATE product_images SET is_primary = 1 WHERE id = #{id}")
    void setPrimaryFlag(Long id);
    
    @Select("SELECT * FROM product_images WHERE id = #{id}")
    @Results({
        @Result(property = "productId", column = "product_id"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "altText", column = "alt_text"),
        @Result(property = "displayOrder", column = "display_order"),
        @Result(property = "isPrimary", column = "is_primary"),
        @Result(property = "createdAt", column = "created_at")
    })
    ProductImage findById(Long id);
    
    @Update("UPDATE product_images SET alt_text = #{altText}, display_order = #{displayOrder} WHERE id = #{id}")
    void update(ProductImage image);
    
    @Delete("DELETE FROM product_images WHERE id = #{id}")
    void delete(Long id);
}