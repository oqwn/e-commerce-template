package com.ecommerce.mapper;

import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface CartMapper {
    
    // Cart CRUD operations
    void insertCart(Cart cart);
    void updateCart(Cart cart);
    void deleteCartById(@Param("id") Long id);
    Optional<Cart> findCartById(@Param("id") Long id);
    Optional<Cart> findCartByUserId(@Param("userId") Long userId);
    Optional<Cart> findCartBySessionId(@Param("sessionId") String sessionId);
    Optional<Cart> findCartWithItems(@Param("id") Long id);
    Optional<Cart> findCartByUserIdWithItems(@Param("userId") Long userId);
    Optional<Cart> findCartBySessionIdWithItems(@Param("sessionId") String sessionId);
    
    // Cart item CRUD operations
    void insertCartItem(CartItem cartItem);
    void updateCartItem(CartItem cartItem);
    void deleteCartItemById(@Param("id") Long id);
    void deleteCartItemsByCartId(@Param("cartId") Long cartId);
    Optional<CartItem> findCartItemById(@Param("id") Long id);
    List<CartItem> findCartItemsByCartId(@Param("cartId") Long cartId);
    List<CartItem> findCartItemsWithProductsByCartId(@Param("cartId") Long cartId);
    
    // Cart item specific operations
    Optional<CartItem> findCartItemByCartAndProduct(@Param("cartId") Long cartId, 
                                                   @Param("productId") Long productId);
    Optional<CartItem> findCartItemByCartProductAndVariants(@Param("cartId") Long cartId, 
                                                           @Param("productId") Long productId,
                                                           @Param("selectedVariants") String selectedVariantsJson);
    void updateCartItemQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    // Cart management
    void updateCartTimestamp(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);
    void clearExpiredCarts(@Param("expiredBefore") LocalDateTime expiredBefore);
    
    // Cart statistics
    int getCartItemCount(@Param("cartId") Long cartId);
    java.math.BigDecimal getCartTotalPrice(@Param("cartId") Long cartId);
    
    // Transfer cart operations (guest to user)
    void transferGuestCartToUser(@Param("sessionId") String sessionId, @Param("userId") Long userId);
    void mergeCartsOnLogin(@Param("guestCartId") Long guestCartId, @Param("userCartId") Long userCartId);
    
    // Check existence
    boolean existsCartById(@Param("id") Long id);
    boolean existsCartByUserId(@Param("userId") Long userId);
    boolean existsCartBySessionId(@Param("sessionId") String sessionId);
}