package com.ecommerce.controller;

import com.ecommerce.dto.*;
import com.ecommerce.service.CartService;
import com.ecommerce.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "*")
public class CartController {
    
    private final CartService cartService;
    
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse> getCart(HttpServletRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            String sessionId = getOrCreateSessionId(request);
            
            CartResponse cart = cartService.getOrCreateCart(userId, sessionId);
            return ResponseEntity.ok(ApiResponse.success("Cart retrieved successfully", cart));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/items")
    public ResponseEntity<ApiResponse> addToCart(
            @RequestBody AddToCartRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            String sessionId = getOrCreateSessionId(httpRequest);
            
            CartResponse cart = cartService.addToCart(userId, sessionId, request);
            return ResponseEntity.ok(ApiResponse.success("Item added to cart", cart));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/items/{itemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> updateCartItem(
            @PathVariable Long itemId,
            @RequestBody UpdateCartItemRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            String sessionId = getOrCreateSessionId(httpRequest);
            
            CartResponse cart = cartService.updateCartItem(userId, sessionId, itemId, request);
            return ResponseEntity.ok(ApiResponse.success("Cart item updated", cart));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse> removeFromCart(
            @PathVariable Long itemId,
            HttpServletRequest httpRequest) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            String sessionId = getOrCreateSessionId(httpRequest);
            
            CartResponse cart = cartService.removeFromCart(userId, sessionId, itemId);
            return ResponseEntity.ok(ApiResponse.success("Item removed from cart", cart));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> clearCart(HttpServletRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            String sessionId = getOrCreateSessionId(request);
            
            cartService.clearCart(userId, sessionId);
            return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/transfer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> transferGuestCart(
            @RequestParam String guestSessionId) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            cartService.transferGuestCartToUser(guestSessionId, userId);
            return ResponseEntity.ok(ApiResponse.success("Guest cart transferred successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<ApiResponse> getCartItemCount(HttpServletRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            String sessionId = getOrCreateSessionId(request);
            
            CartResponse cart = cartService.getOrCreateCart(userId, sessionId);
            return ResponseEntity.ok(ApiResponse.success("Cart count retrieved", cart.getTotalItems()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    private String getOrCreateSessionId(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String sessionId = (String) session.getAttribute("cart_session_id");
        
        if (sessionId == null) {
            sessionId = cartService.generateGuestSessionId();
            session.setAttribute("cart_session_id", sessionId);
        }
        
        return sessionId;
    }
}