package com.ecommerce.service;

import com.ecommerce.dto.CreateReviewRequest;
import com.ecommerce.dto.UpdateReviewRequest;
import com.ecommerce.dto.ProductReviewDto;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.UnauthorizedException;
import com.ecommerce.mapper.ProductReviewMapper;
import com.ecommerce.mapper.UserMapper;
import com.ecommerce.model.ProductReview;
import com.ecommerce.model.User;
import com.ecommerce.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductReviewService {

    private final ProductReviewMapper productReviewMapper;
    private final UserMapper userMapper;

    public ProductReviewDto createReview(CreateReviewRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new UnauthorizedException("Must be logged in to create a review");
        }

        // Check if user has already reviewed this product
        if (productReviewMapper.existsByProductIdAndUserId(request.getProductId(), currentUserId)) {
            throw new IllegalArgumentException("You have already reviewed this product");
        }

        ProductReview review = ProductReview.builder()
                .productId(request.getProductId())
                .userId(currentUserId)
                .rating(request.getRating())
                .title(request.getTitle())
                .comment(request.getComment())
                .isVerifiedPurchase(false) // TODO: Check if user actually purchased this product
                .status(ProductReview.ReviewStatus.PENDING)
                .build();

        productReviewMapper.insert(review);
        log.info("Review created successfully: {}", review.getId());

        return getReviewById(review.getId());
    }

    public ProductReviewDto updateReview(Long reviewId, UpdateReviewRequest request) {
        ProductReview review = productReviewMapper.findById(reviewId);
        if (review == null) {
            throw new ResourceNotFoundException("Review not found");
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!review.getUserId().equals(currentUserId) && !SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("You can only update your own reviews");
        }

        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getTitle() != null) {
            review.setTitle(request.getTitle());
        }
        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }

        productReviewMapper.update(review);
        log.info("Review updated successfully: {}", reviewId);

        return getReviewById(reviewId);
    }

    public ProductReviewDto getReviewById(Long reviewId) {
        ProductReview review = productReviewMapper.findById(reviewId);
        if (review == null) {
            throw new ResourceNotFoundException("Review not found");
        }

        // Load user data
        User user = userMapper.findById(review.getUserId()).orElse(null);
        review.setUser(user);

        return ProductReviewDto.fromProductReview(review);
    }

    public List<ProductReviewDto> getProductReviews(Long productId) {
        List<ProductReview> reviews = productReviewMapper.findByProductId(productId);
        
        return reviews.stream()
                .map(review -> {
                    User user = userMapper.findById(review.getUserId()).orElse(null);
                    review.setUser(user);
                    return ProductReviewDto.fromProductReview(review);
                })
                .collect(Collectors.toList());
    }

    public List<ProductReviewDto> getUserReviews(Long userId) {
        List<ProductReview> reviews = productReviewMapper.findByUserId(userId);
        
        return reviews.stream()
                .map(review -> {
                    User user = userMapper.findById(review.getUserId()).orElse(null);
                    review.setUser(user);
                    return ProductReviewDto.fromProductReview(review);
                })
                .collect(Collectors.toList());
    }

    public List<ProductReviewDto> getPendingReviews() {
        if (!SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Only admins can view pending reviews");
        }

        List<ProductReview> reviews = productReviewMapper.findByStatus("PENDING");
        
        return reviews.stream()
                .map(review -> {
                    User user = userMapper.findById(review.getUserId()).orElse(null);
                    review.setUser(user);
                    return ProductReviewDto.fromProductReview(review);
                })
                .collect(Collectors.toList());
    }

    public void approveReview(Long reviewId) {
        if (!SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Only admins can approve reviews");
        }

        ProductReview review = productReviewMapper.findById(reviewId);
        if (review == null) {
            throw new ResourceNotFoundException("Review not found");
        }

        productReviewMapper.updateStatus(reviewId, "APPROVED");
        log.info("Review approved: {}", reviewId);
    }

    public void rejectReview(Long reviewId) {
        if (!SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Only admins can reject reviews");
        }

        ProductReview review = productReviewMapper.findById(reviewId);
        if (review == null) {
            throw new ResourceNotFoundException("Review not found");
        }

        productReviewMapper.updateStatus(reviewId, "REJECTED");
        log.info("Review rejected: {}", reviewId);
    }

    public void deleteReview(Long reviewId) {
        ProductReview review = productReviewMapper.findById(reviewId);
        if (review == null) {
            throw new ResourceNotFoundException("Review not found");
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!review.getUserId().equals(currentUserId) && !SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("You can only delete your own reviews");
        }

        productReviewMapper.delete(reviewId);
        log.info("Review deleted: {}", reviewId);
    }

    public Map<String, Object> getProductRatingStats(Long productId) {
        return productReviewMapper.getProductRatingStats(productId);
    }
}