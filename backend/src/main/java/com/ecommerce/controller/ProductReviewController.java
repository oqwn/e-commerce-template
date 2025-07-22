package com.ecommerce.controller;

import com.ecommerce.dto.*;
import com.ecommerce.service.ProductReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Product Reviews", description = "Product review management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    @PostMapping
    @PreAuthorize("hasAnyRole('BUYER', 'SELLER', 'ADMIN')")
    @Operation(summary = "Create a new review", description = "Create a review for a product. Users can only review each product once.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Review created successfully",
            content = @Content(schema = @Schema(implementation = ProductReviewDto.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input or already reviewed",
            content = @Content(schema = @Schema(implementation = com.ecommerce.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.ecommerce.dto.ApiResponse> createReview(@Valid @RequestBody CreateReviewRequest request) {
        log.info("Creating review for product: {}", request.getProductId());
        ProductReviewDto review = productReviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.ecommerce.dto.ApiResponse.success("Review created successfully", review));
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('BUYER', 'SELLER', 'ADMIN')")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request) {
        log.info("Updating review: {}", reviewId);
        ProductReviewDto review = productReviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success("Review updated successfully", review));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> getReview(@PathVariable Long reviewId) {
        ProductReviewDto review = productReviewService.getReviewById(reviewId);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success(review));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> getProductReviews(@PathVariable Long productId) {
        List<ProductReviewDto> reviews = productReviewService.getProductReviews(productId);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success(reviews));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> getUserReviews(@PathVariable Long userId) {
        List<ProductReviewDto> reviews = productReviewService.getUserReviews(userId);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success(reviews));
    }

    @GetMapping("/product/{productId}/stats")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> getProductRatingStats(@PathVariable Long productId) {
        Map<String, Object> stats = productReviewService.getProductRatingStats(productId);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success(stats));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> getPendingReviews() {
        List<ProductReviewDto> reviews = productReviewService.getPendingReviews();
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success(reviews));
    }

    @PatchMapping("/{reviewId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> approveReview(@PathVariable Long reviewId) {
        log.info("Approving review: {}", reviewId);
        productReviewService.approveReview(reviewId);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success("Review approved successfully"));
    }

    @PatchMapping("/{reviewId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> rejectReview(@PathVariable Long reviewId) {
        log.info("Rejecting review: {}", reviewId);
        productReviewService.rejectReview(reviewId);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success("Review rejected successfully"));
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('BUYER', 'SELLER', 'ADMIN')")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> deleteReview(@PathVariable Long reviewId) {
        log.info("Deleting review: {}", reviewId);
        productReviewService.deleteReview(reviewId);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success("Review deleted successfully"));
    }
}