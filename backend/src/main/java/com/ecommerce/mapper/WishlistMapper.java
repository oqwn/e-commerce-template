package com.ecommerce.mapper;

import com.ecommerce.model.Wishlist;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface WishlistMapper {

    @Insert("INSERT INTO wishlists (user_id, product_id, notes, priority, created_at) " +
            "VALUES (#{userId}, #{productId}, #{notes}, #{priority}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Wishlist wishlist);

    @Delete("DELETE FROM wishlists WHERE user_id = #{userId} AND product_id = #{productId}")
    void deleteByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    @Select("SELECT EXISTS(SELECT 1 FROM wishlists WHERE user_id = #{userId} AND product_id = #{productId})")
    boolean existsByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    @Select("SELECT COUNT(*) FROM wishlists WHERE user_id = #{userId}")
    long countByUser(@Param("userId") Long userId);

    @Select("SELECT product_id FROM wishlists WHERE user_id = #{userId}")
    List<Long> findProductIdsByUser(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM wishlists WHERE product_id = #{productId}")
    long countByProduct(@Param("productId") Long productId);

    @Update("UPDATE wishlists SET notes = #{notes}, priority = #{priority} " +
            "WHERE user_id = #{userId} AND product_id = #{productId}")
    void updateByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId,
                               @Param("notes") String notes, @Param("priority") Integer priority);

    @Select("SELECT w.*, u.first_name as user_first_name, u.last_name as user_last_name, " +
            "p.name as product_name, p.description as product_description, p.price as product_price, " +
            "pi.image_url as product_image, s.store_name as seller_name " +
            "FROM wishlists w " +
            "JOIN users u ON w.user_id = u.id " +
            "JOIN products p ON w.product_id = p.id " +
            "JOIN stores s ON p.seller_id = s.id " +
            "LEFT JOIN product_images pi ON p.id = pi.product_id AND pi.is_primary = true " +
            "WHERE w.user_id = #{userId} " +
            "ORDER BY w.priority DESC, w.created_at DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<Wishlist> findByUserWithProducts(@Param("userId") Long userId, @Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM wishlists WHERE user_id = #{userId}")
    long countByUserForPagination(@Param("userId") Long userId);

    @Select("SELECT * FROM wishlists WHERE user_id = #{userId} AND product_id = #{productId}")
    Wishlist findByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);
}