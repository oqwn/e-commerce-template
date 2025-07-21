package com.ecommerce.service;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.UnauthorizedException;
import com.ecommerce.mapper.CouponMapper;
import com.ecommerce.mapper.StoreMapper;
import com.ecommerce.model.Coupon;
import com.ecommerce.model.CouponUsage;
import com.ecommerce.model.Store;
import com.ecommerce.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CouponService {

    @Autowired
    private CouponMapper couponMapper;
    
    @Autowired 
    private StoreMapper storeMapper;

    /**
     * Validate and apply coupon to order
     */
    public CouponValidationResult validateAndCalculateDiscount(String couponCode, 
                                                              BigDecimal orderAmount, 
                                                              Long userId,
                                                              Long storeId) {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return new CouponValidationResult(false, "Coupon code is required", BigDecimal.ZERO, null);
        }

        Coupon coupon = couponMapper.findValidCouponByCode(couponCode.toUpperCase(), LocalDateTime.now());
        if (coupon == null) {
            return new CouponValidationResult(false, "Invalid or expired coupon code", BigDecimal.ZERO, null);
        }

        // Validate store-specific coupon
        if (coupon.getStoreId() != null && !coupon.getStoreId().equals(storeId)) {
            return new CouponValidationResult(false, "Coupon is not valid for this store", BigDecimal.ZERO, null);
        }

        // Check minimum order amount
        if (coupon.getMinimumOrderAmount() != null && orderAmount.compareTo(coupon.getMinimumOrderAmount()) < 0) {
            return new CouponValidationResult(false, 
                String.format("Minimum order amount of $%.2f required", coupon.getMinimumOrderAmount()), 
                BigDecimal.ZERO, null);
        }

        // Check per-user usage limit
        if (coupon.getPerUserLimit() != null && userId != null) {
            int userUsageCount = couponMapper.countUsagesByCouponAndUser(coupon.getId(), userId);
            if (userUsageCount >= coupon.getPerUserLimit()) {
                return new CouponValidationResult(false, "Coupon usage limit reached", BigDecimal.ZERO, null);
            }
        }

        // Check total usage limit
        if (coupon.getUsageLimit() != null) {
            int totalUsageCount = couponMapper.countUsagesByCouponId(coupon.getId());
            if (totalUsageCount >= coupon.getUsageLimit()) {
                return new CouponValidationResult(false, "Coupon is no longer available", BigDecimal.ZERO, null);
            }
        }

        // Calculate discount
        BigDecimal discountAmount = coupon.calculateDiscount(orderAmount);
        
        return new CouponValidationResult(true, "Coupon applied successfully", discountAmount, coupon);
    }

    /**
     * Record coupon usage after successful order
     */
    public void recordCouponUsage(Long couponId, Long userId, Long orderId, BigDecimal discountAmount) {
        CouponUsage usage = new CouponUsage();
        usage.setCouponId(couponId);
        usage.setUserId(userId);
        usage.setOrderId(orderId);
        usage.setDiscountAmount(discountAmount);
        usage.setUsedAt(LocalDateTime.now());
        
        couponMapper.insertCouponUsage(usage);
    }

    /**
     * Create new coupon (seller only)
     */
    public ApiResponse createCoupon(Coupon coupon) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new UnauthorizedException("Authentication required");
        }

        // Get seller's store
        Optional<Store> storeOpt = storeMapper.findBySellerId(currentUserId);
        if (storeOpt.isEmpty()) {
            throw new ResourceNotFoundException("Store not found for current user");
        }
        Store store = storeOpt.get();

        // Set store ID and timestamps
        coupon.setStoreId(store.getId());
        coupon.setCreatedAt(LocalDateTime.now());
        coupon.setUpdatedAt(LocalDateTime.now());
        
        // Validate coupon data
        validateCouponData(coupon);
        
        // Ensure code is uppercase
        coupon.setCode(coupon.getCode().toUpperCase());

        couponMapper.insertCoupon(coupon);
        
        return new ApiResponse(true, "Coupon created successfully", coupon);
    }

    /**
     * Get coupons for current seller's store
     */
    public ApiResponse getStoreCoupons(int page, int size) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new UnauthorizedException("Authentication required");
        }

        Optional<Store> storeOpt = storeMapper.findBySellerId(currentUserId);
        if (storeOpt.isEmpty()) {
            throw new ResourceNotFoundException("Store not found for current user");
        }
        Store store = storeOpt.get();

        int offset = page * size;
        List<Coupon> coupons = couponMapper.findCouponsByStoreId(store.getId(), offset, size);
        
        return new ApiResponse(true, "Coupons retrieved successfully", coupons);
    }

    /**
     * Get coupon by ID (seller only - must own the coupon)
     */
    public ApiResponse getCouponById(Long couponId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new UnauthorizedException("Authentication required");
        }

        Coupon coupon = couponMapper.findCouponById(couponId);
        if (coupon == null) {
            throw new ResourceNotFoundException("Coupon not found");
        }

        // Verify ownership
        Optional<Store> storeOpt = storeMapper.findBySellerId(currentUserId);
        if (storeOpt.isEmpty() || !storeOpt.get().getId().equals(coupon.getStoreId())) {
            throw new UnauthorizedException("Access denied");
        }

        return new ApiResponse(true, "Coupon retrieved successfully", coupon);
    }

    /**
     * Update coupon (seller only)
     */
    public ApiResponse updateCoupon(Long couponId, Coupon updatedCoupon) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new UnauthorizedException("Authentication required");
        }

        Coupon existingCoupon = couponMapper.findCouponById(couponId);
        if (existingCoupon == null) {
            throw new ResourceNotFoundException("Coupon not found");
        }

        // Verify ownership
        Optional<Store> storeOpt = storeMapper.findBySellerId(currentUserId);
        if (storeOpt.isEmpty()) {
            throw new UnauthorizedException("Access denied");
        }
        Store store = storeOpt.get();
        if (!store.getId().equals(existingCoupon.getStoreId())) {
            throw new UnauthorizedException("Access denied");
        }

        // Update fields
        existingCoupon.setCode(updatedCoupon.getCode().toUpperCase());
        existingCoupon.setType(updatedCoupon.getType());
        existingCoupon.setDiscountValue(updatedCoupon.getDiscountValue());
        existingCoupon.setMinimumOrderAmount(updatedCoupon.getMinimumOrderAmount());
        existingCoupon.setMaximumDiscountAmount(updatedCoupon.getMaximumDiscountAmount());
        existingCoupon.setValidFrom(updatedCoupon.getValidFrom());
        existingCoupon.setValidUntil(updatedCoupon.getValidUntil());
        existingCoupon.setUsageLimit(updatedCoupon.getUsageLimit());
        existingCoupon.setPerUserLimit(updatedCoupon.getPerUserLimit());
        existingCoupon.setIsActive(updatedCoupon.isActive());
        existingCoupon.setUpdatedAt(LocalDateTime.now());

        validateCouponData(existingCoupon);
        
        couponMapper.updateCoupon(existingCoupon);
        
        return new ApiResponse(true, "Coupon updated successfully", existingCoupon);
    }

    /**
     * Delete coupon (seller only)
     */
    public ApiResponse deleteCoupon(Long couponId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new UnauthorizedException("Authentication required");
        }

        Coupon coupon = couponMapper.findCouponById(couponId);
        if (coupon == null) {
            throw new ResourceNotFoundException("Coupon not found");
        }

        // Verify ownership
        Optional<Store> storeOpt = storeMapper.findBySellerId(currentUserId);
        if (storeOpt.isEmpty() || !storeOpt.get().getId().equals(coupon.getStoreId())) {
            throw new UnauthorizedException("Access denied");
        }

        couponMapper.deleteCoupon(couponId);
        
        return new ApiResponse(true, "Coupon deleted successfully", null);
    }

    /**
     * Get coupon usage statistics
     */
    public ApiResponse getCouponUsages(Long couponId, int page, int size) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new UnauthorizedException("Authentication required");
        }

        Coupon coupon = couponMapper.findCouponById(couponId);
        if (coupon == null) {
            throw new ResourceNotFoundException("Coupon not found");
        }

        // Verify ownership
        Optional<Store> storeOpt = storeMapper.findBySellerId(currentUserId);
        if (storeOpt.isEmpty() || !storeOpt.get().getId().equals(coupon.getStoreId())) {
            throw new UnauthorizedException("Access denied");
        }

        int offset = page * size;
        List<CouponUsage> usages = couponMapper.findUsagesByCouponId(couponId, offset, size);
        
        return new ApiResponse(true, "Coupon usages retrieved successfully", usages);
    }

    /**
     * Validate coupon for checkout (public endpoint for buyers)
     */
    public ApiResponse validateCouponForCheckout(String couponCode, BigDecimal orderAmount) {
        Long userId = SecurityUtils.getCurrentUserId(); // May be null for guest checkout
        
        // Note: storeId would come from order items context - for now assume single-store orders
        CouponValidationResult result = validateAndCalculateDiscount(couponCode, orderAmount, userId, null);
        
        return new ApiResponse(result.isValid(), result.getMessage(), result);
    }

    private void validateCouponData(Coupon coupon) {
        if (coupon.getCode() == null || coupon.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Coupon code is required");
        }
        
        if (coupon.getType() == null) {
            throw new IllegalArgumentException("Coupon type is required");
        }
        
        if (coupon.getDiscountValue() == null || coupon.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Discount value must be greater than 0");
        }
        
        if (coupon.getValidFrom() == null) {
            throw new IllegalArgumentException("Valid from date is required");
        }
        
        if (coupon.getValidUntil() == null) {
            throw new IllegalArgumentException("Valid until date is required");
        }
        
        if (coupon.getValidFrom().isAfter(coupon.getValidUntil())) {
            throw new IllegalArgumentException("Valid from date must be before valid until date");
        }
        
        if (coupon.getType() == Coupon.CouponType.PERCENTAGE && 
            coupon.getDiscountValue().compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Percentage discount cannot exceed 100%");
        }
    }

    /**
     * Result class for coupon validation
     */
    public static class CouponValidationResult {
        private final boolean valid;
        private final String message;
        private final BigDecimal discountAmount;
        private final Coupon coupon;

        public CouponValidationResult(boolean valid, String message, BigDecimal discountAmount, Coupon coupon) {
            this.valid = valid;
            this.message = message;
            this.discountAmount = discountAmount;
            this.coupon = coupon;
        }

        // Getters
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
        public BigDecimal getDiscountAmount() { return discountAmount; }
        public Coupon getCoupon() { return coupon; }
    }
}