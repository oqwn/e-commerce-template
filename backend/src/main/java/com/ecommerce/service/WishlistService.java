package com.ecommerce.service;

import com.ecommerce.dto.request.WishlistRequest;
import com.ecommerce.dto.response.WishlistResponse;
import com.ecommerce.dto.PaginationResponse;

import java.util.List;

public interface WishlistService {
    WishlistResponse addToWishlist(Long userId, WishlistRequest request);
    void removeFromWishlist(Long userId, Long productId);
    PaginationResponse<WishlistResponse> getUserWishlist(Long userId, int page, int size);
    boolean isProductInWishlist(Long userId, Long productId);
    List<Long> getUserWishlistProductIds(Long userId);
    long getWishlistCount(Long userId);
    void updateWishlistItem(Long userId, Long productId, WishlistRequest request);
    long getProductWishlistCount(Long productId);
}