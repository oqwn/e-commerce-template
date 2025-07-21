package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.model.Coupon;
import com.ecommerce.model.CouponUsage;
import com.ecommerce.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@CrossOrigin(origins = "*")
public class CouponController {

    @Autowired
    private CouponService couponService;

    /**
     * Validate coupon for checkout (public endpoint)
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse> validateCoupon(
            @RequestParam String code,
            @RequestParam BigDecimal orderAmount) {
        try {
            ApiResponse response = couponService.validateCouponForCheckout(code, orderAmount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Create new coupon (seller only)
     */
    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse> createCoupon(@RequestBody Coupon coupon) {
        try {
            ApiResponse response = couponService.createCoupon(coupon);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get seller's coupons
     */
    @GetMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse> getStoreCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            ApiResponse response = couponService.getStoreCoupons(page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get coupon by ID (seller only)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse> getCouponById(@PathVariable Long id) {
        try {
            ApiResponse response = couponService.getCouponById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Update coupon (seller only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse> updateCoupon(
            @PathVariable Long id,
            @RequestBody Coupon coupon) {
        try {
            ApiResponse response = couponService.updateCoupon(id, coupon);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Delete coupon (seller only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse> deleteCoupon(@PathVariable Long id) {
        try {
            ApiResponse response = couponService.deleteCoupon(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get coupon usage statistics (seller only)
     */
    @GetMapping("/{id}/usages")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse> getCouponUsages(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            ApiResponse response = couponService.getCouponUsages(id, page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Quick coupon validation (for real-time checkout feedback)
     */
    @GetMapping("/quick-validate")
    public ResponseEntity<ApiResponse> quickValidateCoupon(@RequestParam String code) {
        try {
            // Basic validation without full discount calculation
            ApiResponse result = couponService.validateCouponForCheckout(code, BigDecimal.valueOf(100));
            
            String message = result.isSuccess() ? "Coupon is valid" : result.getMessage();
            return ResponseEntity.ok(new ApiResponse(result.isSuccess(), message, message));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(false, "Invalid coupon code", null));
        }
    }
}