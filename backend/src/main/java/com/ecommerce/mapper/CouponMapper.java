package com.ecommerce.mapper;

import com.ecommerce.model.Coupon;
import com.ecommerce.model.CouponUsage;
import org.apache.ibatis.annotations.*;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CouponMapper {
    
    // Coupon CRUD operations
    @Insert({
        "INSERT INTO coupons (code, type, discount_value, minimum_order_amount, maximum_discount_amount,",
        "valid_from, valid_until, usage_limit, per_user_limit, is_active, store_id, created_at, updated_at)",
        "VALUES (#{code}, #{type}, #{discountValue}, #{minimumOrderAmount}, #{maximumDiscountAmount},",
        "#{validFrom}, #{validUntil}, #{usageLimit}, #{perUserLimit}, #{isActive}, #{storeId}, #{createdAt}, #{updatedAt})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertCoupon(Coupon coupon);
    
    @Select({
        "SELECT c.*, ",
        "(SELECT COUNT(*) FROM coupon_usages cu WHERE cu.coupon_id = c.id) as used_count",
        "FROM coupons c WHERE c.id = #{id}"
    })
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "code", column = "code"),
        @Result(property = "type", column = "type"),
        @Result(property = "discountValue", column = "discount_value"),
        @Result(property = "minimumOrderAmount", column = "minimum_order_amount"),
        @Result(property = "maximumDiscountAmount", column = "maximum_discount_amount"),
        @Result(property = "validFrom", column = "valid_from"),
        @Result(property = "validUntil", column = "valid_until"),
        @Result(property = "usageLimit", column = "usage_limit"),
        @Result(property = "perUserLimit", column = "per_user_limit"),
        @Result(property = "isActive", column = "is_active"),
        @Result(property = "storeId", column = "store_id"),
        @Result(property = "usedCount", column = "used_count"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    Coupon findCouponById(@Param("id") Long id);
    
    @Select({
        "SELECT c.*, ",
        "(SELECT COUNT(*) FROM coupon_usages cu WHERE cu.coupon_id = c.id) as used_count",
        "FROM coupons c WHERE c.code = #{code} AND c.is_active = 1"
    })
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "code", column = "code"),
        @Result(property = "type", column = "type"),
        @Result(property = "discountValue", column = "discount_value"),
        @Result(property = "minimumOrderAmount", column = "minimum_order_amount"),
        @Result(property = "maximumDiscountAmount", column = "maximum_discount_amount"),
        @Result(property = "validFrom", column = "valid_from"),
        @Result(property = "validUntil", column = "valid_until"),
        @Result(property = "usageLimit", column = "usage_limit"),
        @Result(property = "perUserLimit", column = "per_user_limit"),
        @Result(property = "isActive", column = "is_active"),
        @Result(property = "storeId", column = "store_id"),
        @Result(property = "usedCount", column = "used_count"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    Coupon findCouponByCode(@Param("code") String code);
    
    @Select({
        "SELECT c.*, ",
        "(SELECT COUNT(*) FROM coupon_usages cu WHERE cu.coupon_id = c.id) as used_count",
        "FROM coupons c WHERE c.store_id = #{storeId}",
        "ORDER BY c.created_at DESC",
        "LIMIT #{limit} OFFSET #{offset}"
    })
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "code", column = "code"),
        @Result(property = "type", column = "type"),
        @Result(property = "discountValue", column = "discount_value"),
        @Result(property = "minimumOrderAmount", column = "minimum_order_amount"),
        @Result(property = "maximumDiscountAmount", column = "maximum_discount_amount"),
        @Result(property = "validFrom", column = "valid_from"),
        @Result(property = "validUntil", column = "valid_until"),
        @Result(property = "usageLimit", column = "usage_limit"),
        @Result(property = "perUserLimit", column = "per_user_limit"),
        @Result(property = "isActive", column = "is_active"),
        @Result(property = "storeId", column = "store_id"),
        @Result(property = "usedCount", column = "used_count"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<Coupon> findCouponsByStoreId(@Param("storeId") Long storeId, 
                                      @Param("offset") int offset, 
                                      @Param("limit") int limit);
    
    @Update({
        "UPDATE coupons SET code = #{code}, type = #{type}, discount_value = #{discountValue},",
        "minimum_order_amount = #{minimumOrderAmount}, maximum_discount_amount = #{maximumDiscountAmount},",
        "valid_from = #{validFrom}, valid_until = #{validUntil}, usage_limit = #{usageLimit},",
        "per_user_limit = #{perUserLimit}, is_active = #{isActive}, updated_at = #{updatedAt}",
        "WHERE id = #{id}"
    })
    int updateCoupon(Coupon coupon);
    
    @Delete("DELETE FROM coupons WHERE id = #{id}")
    int deleteCoupon(@Param("id") Long id);
    
    @Select("SELECT COUNT(*) FROM coupons WHERE store_id = #{storeId}")
    int countCouponsByStoreId(@Param("storeId") Long storeId);
    
    // Coupon Usage operations
    @Insert({
        "INSERT INTO coupon_usages (coupon_id, user_id, order_id, discount_amount, used_at)",
        "VALUES (#{couponId}, #{userId}, #{orderId}, #{discountAmount}, #{usedAt})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertCouponUsage(CouponUsage couponUsage);
    
    @Select("SELECT COUNT(*) FROM coupon_usages WHERE coupon_id = #{couponId}")
    int countUsagesByCouponId(@Param("couponId") Long couponId);
    
    @Select("SELECT COUNT(*) FROM coupon_usages WHERE coupon_id = #{couponId} AND user_id = #{userId}")
    int countUsagesByCouponAndUser(@Param("couponId") Long couponId, @Param("userId") Long userId);
    
    @Select({
        "SELECT cu.*, c.code as coupon_code FROM coupon_usages cu",
        "JOIN coupons c ON cu.coupon_id = c.id",
        "WHERE cu.coupon_id = #{couponId}",
        "ORDER BY cu.used_at DESC",
        "LIMIT #{limit} OFFSET #{offset}"
    })
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "couponId", column = "coupon_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "discountAmount", column = "discount_amount"),
        @Result(property = "usedAt", column = "used_at")
    })
    List<CouponUsage> findUsagesByCouponId(@Param("couponId") Long couponId,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);
    
    @Select({
        "SELECT cu.*, c.code as coupon_code FROM coupon_usages cu",
        "JOIN coupons c ON cu.coupon_id = c.id",
        "WHERE cu.user_id = #{userId}",
        "ORDER BY cu.used_at DESC",
        "LIMIT #{limit} OFFSET #{offset}"
    })
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "couponId", column = "coupon_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "discountAmount", column = "discount_amount"),
        @Result(property = "usedAt", column = "used_at")
    })
    List<CouponUsage> findUsagesByUserId(@Param("userId") Long userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);
                                         
    @Select("SELECT cu.* FROM coupon_usages cu WHERE cu.order_id = #{orderId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "couponId", column = "coupon_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "discountAmount", column = "discount_amount"),
        @Result(property = "usedAt", column = "used_at")
    })
    CouponUsage findUsageByOrderId(@Param("orderId") Long orderId);
    
    // Statistics and validation queries
    @Select({
        "SELECT c.* FROM coupons c WHERE c.code = #{code}",
        "AND c.is_active = 1",
        "AND c.valid_from <= #{now}",
        "AND c.valid_until >= #{now}",
        "AND (c.usage_limit IS NULL OR ",
        "     (SELECT COUNT(*) FROM coupon_usages cu WHERE cu.coupon_id = c.id) < c.usage_limit)"
    })
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "code", column = "code"),
        @Result(property = "type", column = "type"),
        @Result(property = "discountValue", column = "discount_value"),
        @Result(property = "minimumOrderAmount", column = "minimum_order_amount"),
        @Result(property = "maximumDiscountAmount", column = "maximum_discount_amount"),
        @Result(property = "validFrom", column = "valid_from"),
        @Result(property = "validUntil", column = "valid_until"),
        @Result(property = "usageLimit", column = "usage_limit"),
        @Result(property = "perUserLimit", column = "per_user_limit"),
        @Result(property = "isActive", column = "is_active"),
        @Result(property = "storeId", column = "store_id"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    Coupon findValidCouponByCode(@Param("code") String code, @Param("now") LocalDateTime now);
}