package com.ecommerce.mapper;

import com.ecommerce.model.ProductReview;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductReviewMapper {

    @Insert("INSERT INTO product_reviews (product_id, user_id, rating, title, comment, is_verified_purchase, status) " +
            "VALUES (#{productId}, #{userId}, #{rating}, #{title}, #{comment}, #{isVerifiedPurchase}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ProductReview review);

    @Select("SELECT * FROM product_reviews WHERE id = #{id}")
    @Results({
        @Result(property = "productId", column = "product_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "isVerifiedPurchase", column = "is_verified_purchase"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    ProductReview findById(Long id);

    @Select("SELECT * FROM product_reviews WHERE product_id = #{productId} AND status = 'APPROVED' ORDER BY created_at DESC")
    @Results({
        @Result(property = "productId", column = "product_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "isVerifiedPurchase", column = "is_verified_purchase"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<ProductReview> findByProductId(Long productId);

    @Select("SELECT * FROM product_reviews WHERE user_id = #{userId} ORDER BY created_at DESC")
    @Results({
        @Result(property = "productId", column = "product_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "isVerifiedPurchase", column = "is_verified_purchase"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<ProductReview> findByUserId(Long userId);

    @Select("SELECT * FROM product_reviews WHERE status = #{status} ORDER BY created_at DESC")
    @Results({
        @Result(property = "productId", column = "product_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "isVerifiedPurchase", column = "is_verified_purchase"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<ProductReview> findByStatus(String status);

    @Select("SELECT COUNT(*) > 0 FROM product_reviews WHERE product_id = #{productId} AND user_id = #{userId}")
    boolean existsByProductIdAndUserId(Long productId, Long userId);

    @Update("UPDATE product_reviews SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") String status);

    @Update("UPDATE product_reviews SET rating = #{rating}, title = #{title}, comment = #{comment}, updated_at = NOW() WHERE id = #{id}")
    void update(ProductReview review);

    @Delete("DELETE FROM product_reviews WHERE id = #{id}")
    void delete(Long id);

    @Select("SELECT AVG(rating) as avgRating, COUNT(*) as totalReviews " +
            "FROM product_reviews WHERE product_id = #{productId} AND status = 'APPROVED'")
    Map<String, Object> getProductRatingStats(Long productId);
}