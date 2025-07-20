package com.ecommerce.mapper;

import com.ecommerce.model.ProductAttribute;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductAttributeMapper {
    
    @Insert("INSERT INTO product_attributes (product_id, attribute_name, attribute_value) " +
            "VALUES (#{productId}, #{attributeName}, #{attributeValue})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ProductAttribute attribute);
    
    @Select("SELECT * FROM product_attributes WHERE product_id = #{productId}")
    @Results({
        @Result(property = "productId", column = "product_id"),
        @Result(property = "attributeName", column = "attribute_name"),
        @Result(property = "attributeValue", column = "attribute_value"),
        @Result(property = "createdAt", column = "created_at")
    })
    List<ProductAttribute> findByProductId(Long productId);
    
    @Delete("DELETE FROM product_attributes WHERE product_id = #{productId}")
    void deleteByProductId(Long productId);
    
    @Update("UPDATE product_attributes SET attribute_value = #{value} " +
            "WHERE product_id = #{productId} AND attribute_name = #{name}")
    void updateAttribute(@Param("productId") Long productId, 
                        @Param("name") String name, 
                        @Param("value") String value);
    
    @Select("SELECT DISTINCT attribute_name FROM product_attributes")
    List<String> findAllAttributeNames();
}