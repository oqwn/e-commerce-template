package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.PaginationResponse;
import com.ecommerce.dto.request.WishlistRequest;
import com.ecommerce.dto.response.WishlistResponse;
import com.ecommerce.service.WishlistService;
import com.ecommerce.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "Wishlist management APIs")
@SecurityRequirement(name = "bearerAuth")
public class WishlistController {
    
    private final WishlistService wishlistService;
    
    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    @Operation(summary = "Add product to wishlist", description = "Adds a product to the authenticated user's wishlist")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Product added to wishlist",
            content = @Content(schema = @Schema(implementation = WishlistResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ApiResponse> addToWishlist(
            @Valid @RequestBody WishlistRequest request) {
        
        Long userId = SecurityUtils.getCurrentUserId();
        WishlistResponse response = wishlistService.addToWishlist(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product added to wishlist", response));
    }
    
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('BUYER')")
    @Operation(summary = "Remove product from wishlist", description = "Removes a product from the authenticated user's wishlist")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product removed from wishlist"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found in wishlist")
    })
    public ResponseEntity<ApiResponse> removeFromWishlist(
            @Parameter(description = "Product ID to remove") @PathVariable Long productId) {
        
        Long userId = SecurityUtils.getCurrentUserId();
        wishlistService.removeFromWishlist(userId, productId);
        return ResponseEntity.ok(ApiResponse.success("Product removed from wishlist"));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('BUYER')")
    @Operation(summary = "Get user wishlist", description = "Retrieves the authenticated user's wishlist with pagination")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Wishlist retrieved successfully")
    })
    public ResponseEntity<ApiResponse> getUserWishlist(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Long userId = SecurityUtils.getCurrentUserId();
        PaginationResponse<WishlistResponse> wishlist = wishlistService.getUserWishlist(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success("Wishlist retrieved successfully", wishlist));
    }
    
    @GetMapping("/check/{productId}")
    @PreAuthorize("hasRole('BUYER')")
    @Operation(summary = "Check if product is in wishlist", description = "Checks if a specific product is in the user's wishlist")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Check completed")
    })
    public ResponseEntity<ApiResponse> isProductInWishlist(
            @Parameter(description = "Product ID to check") @PathVariable Long productId) {
        
        Long userId = SecurityUtils.getCurrentUserId();
        boolean inWishlist = wishlistService.isProductInWishlist(userId, productId);
        return ResponseEntity.ok(ApiResponse.success("Check completed", inWishlist));
    }
    
    @GetMapping("/product-ids")
    @PreAuthorize("hasRole('BUYER')")
    @Operation(summary = "Get wishlist product IDs", description = "Retrieves all product IDs in the user's wishlist")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product IDs retrieved")
    })
    public ResponseEntity<ApiResponse> getWishlistProductIds() {
        
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> productIds = wishlistService.getUserWishlistProductIds(userId);
        return ResponseEntity.ok(ApiResponse.success("Product IDs retrieved", productIds));
    }
    
    @GetMapping("/count")
    @PreAuthorize("hasRole('BUYER')")
    @Operation(summary = "Get wishlist count", description = "Gets the total number of items in the user's wishlist")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Count retrieved")
    })
    public ResponseEntity<ApiResponse> getWishlistCount() {
        
        Long userId = SecurityUtils.getCurrentUserId();
        long count = wishlistService.getWishlistCount(userId);
        return ResponseEntity.ok(ApiResponse.success("Wishlist count retrieved", count));
    }
    
    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('BUYER')")
    @Operation(summary = "Update wishlist item", description = "Updates notes or priority for a wishlist item")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Wishlist item updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Wishlist item not found")
    })
    public ResponseEntity<ApiResponse> updateWishlistItem(
            @Parameter(description = "Product ID to update") @PathVariable Long productId,
            @Valid @RequestBody WishlistRequest request) {
        
        Long userId = SecurityUtils.getCurrentUserId();
        wishlistService.updateWishlistItem(userId, productId, request);
        return ResponseEntity.ok(ApiResponse.success("Wishlist item updated"));
    }
    
    @GetMapping("/product/{productId}/count")
    @Operation(summary = "Get product wishlist count", description = "Gets the number of users who have added this product to their wishlist")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Count retrieved")
    })
    public ResponseEntity<ApiResponse> getProductWishlistCount(
            @Parameter(description = "Product ID") @PathVariable Long productId) {
        
        long count = wishlistService.getProductWishlistCount(productId);
        return ResponseEntity.ok(ApiResponse.success("Product wishlist count retrieved", count));
    }
}