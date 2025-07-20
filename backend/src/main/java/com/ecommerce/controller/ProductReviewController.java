package com.ecommerce.controller;

import com.ecommerce.dto.*;
import com.ecommerce.service.ProductReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    @PostMapping
    @PreAuthorize("hasAnyRole('BUYER', 'SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse> createReview(@Valid @RequestBody CreateReviewRequest request) {
        log.info("Creating review for product: {}", request.getProductId());
        ProductReviewDto review = productReviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review created successfully", review));
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('BUYER', 'SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request) {
        log.info("Updating review: {}", reviewId);
        ProductReviewDto review = productReviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(ApiResponse.success("Review updated successfully", review));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse> getReview(@PathVariable Long reviewId) {
        ProductReviewDto review = productReviewService.getReviewById(reviewId);
        return ResponseEntity.ok(ApiResponse.success(review));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse> getProductReviews(@PathVariable Long productId) {
        List<ProductReviewDto> reviews = productReviewService.getProductReviews(productId);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserReviews(@PathVariable Long userId) {
        List<ProductReviewDto> reviews = productReviewService.getUserReviews(userId);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @GetMapping("/product/{productId}/stats")
    public ResponseEntity<ApiResponse> getProductRatingStats(@PathVariable Long productId) {
        Map<String, Object> stats = productReviewService.getProductRatingStats(productId);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getPendingReviews() {
        List<ProductReviewDto> reviews = productReviewService.getPendingReviews();
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @PatchMapping("/{reviewId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> approveReview(@PathVariable Long reviewId) {
        log.info("Approving review: {}", reviewId);
        productReviewService.approveReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success("Review approved successfully"));
    }

    @PatchMapping("/{reviewId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> rejectReview(@PathVariable Long reviewId) {
        log.info("Rejecting review: {}", reviewId);
        productReviewService.rejectReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success("Review rejected successfully"));
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('BUYER', 'SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse> deleteReview(@PathVariable Long reviewId) {
        log.info("Deleting review: {}", reviewId);
        productReviewService.deleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully"));
    }
}