/* eslint-disable react-refresh/only-export-components */
import React, { createContext, useState, useEffect, useCallback } from 'react';
import api from '@/services/api';
import { toast } from 'react-toastify';

interface CartItem {
  id: number;
  productId: number;
  productName: string;
  productImage?: string;
  productImageUrl?: string;
  price?: number;
  priceAtTime?: number;
  quantity: number;
  selectedVariants?: any;
  totalPrice: number;
  storeName?: string;
  storeId?: number;
  inStock?: boolean;
  availableQuantity?: number;
}

interface Cart {
  id: number;
  items: CartItem[];
  totalItems: number;
  totalPrice: number;
  expiresAt?: string;
}

interface CartContextType {
  cart: Cart | null;
  loading: boolean;
  // eslint-disable-next-line no-unused-vars
  addToCart: (productId: number, quantity: number, variants?: any) => Promise<void>;
  // eslint-disable-next-line no-unused-vars
  updateCartItem: (itemId: number, quantity: number) => Promise<void>;
  // eslint-disable-next-line no-unused-vars
  removeFromCart: (itemId: number) => Promise<void>;
  clearCart: () => Promise<void>;
  refreshCart: () => Promise<void>;
  cartItemCount: number;
}

export const CartContext = createContext<CartContextType | undefined>(undefined);

export const CartProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [cart, setCart] = useState<Cart | null>(null);
  const [loading, setLoading] = useState(false);

  const fetchCart = useCallback(async () => {
    try {
      setLoading(true);
      const response = await api.get('/cart');
      if (response.success) {
        setCart(response.data);
      }
    } catch (error) {
      console.error('Failed to fetch cart:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchCart();
  }, [fetchCart]);

  const addToCart = async (productId: number, quantity: number, variants?: any) => {
    try {
      setLoading(true);
      const response = await api.post('/cart/items', {
        productId,
        quantity,
        selectedVariants: variants
      });
      if (response.success) {
        setCart(response.data);
        toast.success('Item added to cart');
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to add item to cart');
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const updateCartItem = async (itemId: number, quantity: number) => {
    try {
      setLoading(true);
      const response = await api.put(`/cart/items/${itemId}`, { quantity });
      if (response.success) {
        setCart(response.data);
        toast.success('Cart updated');
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to update cart');
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const removeFromCart = async (itemId: number) => {
    try {
      setLoading(true);
      const response = await api.delete(`/cart/items/${itemId}`);
      if (response.success) {
        setCart(response.data);
        toast.success('Item removed from cart');
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to remove item');
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const clearCart = async () => {
    try {
      setLoading(true);
      const response = await api.delete('/cart');
      if (response.success) {
        setCart(null);
        toast.success('Cart cleared');
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to clear cart');
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const cartItemCount = cart?.totalItems || 0;

  return (
    <CartContext.Provider
      value={{
        cart,
        loading,
        addToCart,
        updateCartItem,
        removeFromCart,
        clearCart,
        refreshCart: fetchCart,
        cartItemCount
      }}
    >
      {children}
    </CartContext.Provider>
  );
};