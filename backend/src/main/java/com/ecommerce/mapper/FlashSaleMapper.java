package com.ecommerce.mapper;

import com.ecommerce.model.FlashSale;
import com.ecommerce.model.FlashSaleProduct;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface FlashSaleMapper {

    @Insert("INSERT INTO flash_sales (name, description, start_time, end_time, discount_percentage, " +
            "max_quantity, is_active, created_by, created_at, updated_at) " +
            "VALUES (#{name}, #{description}, #{startTime}, #{endTime}, #{discountPercentage}, " +
            "#{maxQuantity}, #{isActive}, #{createdBy}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertFlashSale(FlashSale flashSale);

    @Insert("INSERT INTO flash_sale_products (flash_sale_id, product_id, original_price, sale_price, " +
            "max_quantity_per_product, created_at) " +
            "VALUES (#{flashSaleId}, #{productId}, #{originalPrice}, #{salePrice}, " +
            "#{maxQuantityPerProduct}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertFlashSaleProduct(FlashSaleProduct flashSaleProduct);

    @Update("UPDATE flash_sales SET name = #{name}, description = #{description}, " +
            "start_time = #{startTime}, end_time = #{endTime}, discount_percentage = #{discountPercentage}, " +
            "max_quantity = #{maxQuantity}, is_active = #{isActive}, updated_at = NOW() " +
            "WHERE id = #{id}")
    void updateFlashSale(FlashSale flashSale);

    @Update("UPDATE flash_sales SET is_active = #{isActive}, updated_at = NOW() WHERE id = #{id}")
    void updateFlashSaleStatus(@Param("id") Long id, @Param("isActive") Boolean isActive);

    @Update("UPDATE flash_sales SET used_quantity = used_quantity + #{quantity}, updated_at = NOW() " +
            "WHERE id = #{id}")
    void incrementUsedQuantity(@Param("id") Long id, @Param("quantity") int quantity);

    @Update("UPDATE flash_sale_products SET used_quantity_per_product = used_quantity_per_product + #{quantity} " +
            "WHERE id = #{id}")
    void incrementProductUsedQuantity(@Param("id") Long id, @Param("quantity") int quantity);

    @Select("SELECT * FROM flash_sales WHERE id = #{id}")
    FlashSale findById(@Param("id") Long id);

    @Select("SELECT fs.*, CONCAT(u.first_name, ' ', u.last_name) as creator_name " +
            "FROM flash_sales fs " +
            "JOIN users u ON fs.created_by = u.id " +
            "WHERE fs.id = #{id}")
    FlashSale findByIdWithCreator(@Param("id") Long id);

    @Select("SELECT * FROM flash_sales " +
            "WHERE is_active = true AND start_time <= NOW() AND end_time > NOW() " +
            "ORDER BY end_time ASC")
    List<FlashSale> findActiveFlashSales();

    @Select("SELECT * FROM flash_sales " +
            "WHERE is_active = true AND start_time > NOW() " +
            "ORDER BY start_time ASC")
    List<FlashSale> findUpcomingFlashSales();

    @Select("SELECT * FROM flash_sales " +
            "WHERE created_by = #{createdBy} " +
            "ORDER BY created_at DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<FlashSale> findByCreator(@Param("createdBy") Long createdBy, @Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM flash_sales WHERE created_by = #{createdBy}")
    long countByCreator(@Param("createdBy") Long createdBy);

    @Select("SELECT * FROM flash_sales " +
            "ORDER BY created_at DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<FlashSale> findAll(@Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM flash_sales")
    long countAll();

    @Select("SELECT fsp.*, p.name as product_name, p.slug as product_slug, " +
            "pi.image_url as product_image, s.store_name as seller_name " +
            "FROM flash_sale_products fsp " +
            "JOIN products p ON fsp.product_id = p.id " +
            "JOIN stores s ON p.seller_id = s.seller_id " +
            "LEFT JOIN product_images pi ON p.id = pi.product_id AND pi.is_primary = true " +
            "WHERE fsp.flash_sale_id = #{flashSaleId}")
    List<FlashSaleProduct> findProductsByFlashSaleId(@Param("flashSaleId") Long flashSaleId);

    @Select("SELECT fsp.*, fs.name as flash_sale_name, fs.start_time, fs.end_time " +
            "FROM flash_sale_products fsp " +
            "JOIN flash_sales fs ON fsp.flash_sale_id = fs.id " +
            "WHERE fsp.product_id = #{productId} AND fs.is_active = true " +
            "AND fs.start_time <= NOW() AND fs.end_time > NOW() " +
            "ORDER BY fs.end_time ASC " +
            "LIMIT 1")
    FlashSaleProduct findActiveFlashSaleByProductId(@Param("productId") Long productId);

    @Select("SELECT DISTINCT fsp.id, fsp.flash_sale_id, fsp.product_id, fsp.original_price, fsp.sale_price, " +
            "fsp.max_quantity_per_product, fsp.used_quantity_per_product, fsp.created_at, " +
            "p.name as productName, p.slug as productSlug, pi.image_url as productImage, " +
            "s.store_name as sellerName, fs.name as flashSaleName, fs.start_time as flashSaleStartTime, " +
            "fs.end_time as flashSaleEndTime " +
            "FROM flash_sale_products fsp " +
            "JOIN flash_sales fs ON fsp.flash_sale_id = fs.id " +
            "JOIN products p ON fsp.product_id = p.id " +
            "JOIN stores s ON p.seller_id = s.seller_id " +
            "LEFT JOIN product_images pi ON p.id = pi.product_id AND pi.is_primary = true " +
            "WHERE fs.is_active = true AND fs.start_time <= NOW() AND fs.end_time > NOW() " +
            "AND (fsp.max_quantity_per_product IS NULL OR fsp.used_quantity_per_product < fsp.max_quantity_per_product) " +
            "ORDER BY fs.end_time ASC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<FlashSaleProduct> findActiveFlashSaleProducts(@Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT COUNT(DISTINCT fsp.product_id) " +
            "FROM flash_sale_products fsp " +
            "JOIN flash_sales fs ON fsp.flash_sale_id = fs.id " +
            "WHERE fs.is_active = true AND fs.start_time <= NOW() AND fs.end_time > NOW() " +
            "AND (fsp.max_quantity_per_product IS NULL OR fsp.used_quantity_per_product < fsp.max_quantity_per_product)")
    long countActiveFlashSaleProducts();

    @Delete("DELETE FROM flash_sale_products WHERE flash_sale_id = #{flashSaleId}")
    void deleteFlashSaleProducts(@Param("flashSaleId") Long flashSaleId);

    @Delete("DELETE FROM flash_sales WHERE id = #{id}")
    void deleteFlashSale(@Param("id") Long id);

    @Delete("DELETE FROM flash_sale_products WHERE id = #{id}")
    void deleteFlashSaleProduct(@Param("id") Long id);
}