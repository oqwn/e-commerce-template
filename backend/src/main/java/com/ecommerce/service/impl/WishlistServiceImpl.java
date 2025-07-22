package com.ecommerce.service.impl;

import com.ecommerce.dto.PaginationResponse;
import com.ecommerce.dto.request.WishlistRequest;
import com.ecommerce.dto.response.WishlistResponse;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.mapper.UserMapper;
import com.ecommerce.mapper.WishlistMapper;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.model.Wishlist;
import com.ecommerce.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {
    
    private final WishlistMapper wishlistMapper;
    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    
    @Override
    @Transactional
    public WishlistResponse addToWishlist(Long userId, WishlistRequest request) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        Product product = productMapper.findById(request.getProductId());
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        
        // Check if already in wishlist
        if (wishlistMapper.existsByUserAndProduct(userId, request.getProductId())) {
            log.info("Product {} already in user {} wishlist", request.getProductId(), userId);
            Wishlist existing = wishlistMapper.findByUserAndProduct(userId, request.getProductId());
            return convertToResponse(existing);
        }
        
        Wishlist wishlist = Wishlist.builder()
                .userId(userId)
                .productId(request.getProductId())
                .notes(request.getNotes())
                .priority(request.getPriority() != null ? request.getPriority() : 0)
                .createdAt(LocalDateTime.now())
                .build();
                
        wishlistMapper.insert(wishlist);
        log.info("Added product {} to user {} wishlist", request.getProductId(), userId);
        
        // Fetch the inserted record with joined data
        wishlist = wishlistMapper.findByUserAndProduct(userId, request.getProductId());
        return convertToResponse(wishlist);
    }
    
    @Override
    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
                
        wishlistMapper.deleteByUserAndProduct(userId, productId);
        log.info("Removed product {} from user {} wishlist", productId, userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<WishlistResponse> getUserWishlist(Long userId, int page, int size) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        int offset = page * size;
        List<Wishlist> wishlistItems = wishlistMapper.findByUserWithProducts(userId, size, offset);
        long totalElements = wishlistMapper.countByUserForPagination(userId);
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        List<WishlistResponse> content = wishlistItems.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PaginationResponse.<WishlistResponse>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(page == 0)
                .last(page >= totalPages - 1)
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isProductInWishlist(Long userId, Long productId) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
                
        return wishlistMapper.existsByUserAndProduct(userId, productId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Long> getUserWishlistProductIds(Long userId) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        return wishlistMapper.findProductIdsByUser(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getWishlistCount(Long userId) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        return wishlistMapper.countByUser(userId);
    }
    
    @Override
    @Transactional
    public void updateWishlistItem(Long userId, Long productId, WishlistRequest request) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
                
        if (!wishlistMapper.existsByUserAndProduct(userId, productId)) {
            throw new ResourceNotFoundException("Wishlist item not found");
        }
        
        wishlistMapper.updateByUserAndProduct(userId, productId, request.getNotes(), request.getPriority());
        log.info("Updated wishlist item for product {} and user {}", productId, userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getProductWishlistCount(Long productId) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
                
        return wishlistMapper.countByProduct(productId);
    }
    
    private WishlistResponse convertToResponse(Wishlist wishlist) {
        return WishlistResponse.builder()
                .id(wishlist.getId())
                .productId(wishlist.getProductId())
                .productName(wishlist.getProductName())
                .productDescription(wishlist.getProductDescription())
                .productPrice(new BigDecimal(wishlist.getProductPrice() != null ? wishlist.getProductPrice() : "0"))
                .productImage(wishlist.getProductImage())
                .sellerName(wishlist.getSellerName())
                .notes(wishlist.getNotes())
                .priority(wishlist.getPriority())
                .createdAt(wishlist.getCreatedAt())
                .build();
    }
}