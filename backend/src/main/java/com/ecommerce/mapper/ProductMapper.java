package com.ecommerce.mapper;

import com.ecommerce.model.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper {
    
    @Select("SELECT * FROM products WHERE id = #{id}")
    @Results(id = "productResultMap", value = {
        @Result(property = "sellerId", column = "seller_id"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "shortDescription", column = "short_description"),
        @Result(property = "compareAtPrice", column = "compare_at_price"),
        @Result(property = "trackQuantity", column = "track_quantity"),
        @Result(property = "weightUnit", column = "weight_unit"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "publishedAt", column = "published_at")
    })
    Product findById(Long id);
    
    @Select("SELECT * FROM products WHERE slug = #{slug}")
    @ResultMap("productResultMap")
    Product findBySlug(String slug);
    
    @Select("SELECT * FROM products WHERE seller_id = #{sellerId}")
    @ResultMap("productResultMap")
    List<Product> findBySellerId(Long sellerId);
    
    @Select("SELECT * FROM products WHERE category_id = #{categoryId}")
    @ResultMap("productResultMap")
    List<Product> findByCategoryId(Long categoryId);
    
    @Insert("INSERT INTO products (seller_id, category_id, name, slug, description, short_description, " +
            "sku, price, compare_at_price, cost, quantity, track_quantity, weight, weight_unit, " +
            "status, featured) VALUES (#{sellerId}, #{categoryId}, #{name}, #{slug}, #{description}, " +
            "#{shortDescription}, #{sku}, #{price}, #{compareAtPrice}, #{cost}, #{quantity}, " +
            "#{trackQuantity}, #{weight}, #{weightUnit}, #{status}, #{featured})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Product product);
    
    @Update("UPDATE products SET category_id = #{categoryId}, name = #{name}, slug = #{slug}, " +
            "description = #{description}, short_description = #{shortDescription}, sku = #{sku}, " +
            "price = #{price}, compare_at_price = #{compareAtPrice}, cost = #{cost}, " +
            "quantity = #{quantity}, track_quantity = #{trackQuantity}, weight = #{weight}, " +
            "weight_unit = #{weightUnit}, status = #{status}, featured = #{featured}, " +
            "published_at = #{publishedAt} WHERE id = #{id}")
    void update(Product product);
    
    @Delete("DELETE FROM products WHERE id = #{id}")
    void delete(Long id);
    
    @Update("UPDATE products SET quantity = quantity + #{quantity} WHERE id = #{productId}")
    void updateQuantity(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    @Update("UPDATE products SET status = #{status} WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") String status);
    
    List<Product> search(Map<String, Object> params);
    
    @Select("SELECT COUNT(*) FROM products WHERE seller_id = #{sellerId}")
    Integer countBySellerId(Long sellerId);
    
    @Select("SELECT COUNT(*) FROM products WHERE category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);
    
    @Select("SELECT p.*, " +
            "(SELECT AVG(rating) FROM product_reviews WHERE product_id = p.id AND status = 'APPROVED') as average_rating, " +
            "(SELECT COUNT(*) FROM product_reviews WHERE product_id = p.id AND status = 'APPROVED') as review_count " +
            "FROM products p WHERE p.id = #{id}")
    @ResultMap("productResultMap")
    Product findByIdWithStats(Long id);
    
    @Select("SELECT p.* FROM products p WHERE p.featured = 1 AND p.status = 'ACTIVE' " +
            "ORDER BY p.created_at DESC LIMIT #{limit}")
    @ResultMap("productResultMap")
    List<Product> findFeaturedProducts(Integer limit);
    
    @Select("SELECT p.* FROM products p WHERE p.status = 'ACTIVE' " +
            "ORDER BY p.created_at DESC LIMIT #{limit}")
    @ResultMap("productResultMap")
    List<Product> findLatestProducts(Integer limit);
}