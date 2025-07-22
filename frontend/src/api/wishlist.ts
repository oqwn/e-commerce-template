import api from '@/services/api';

// Define ApiResponse interfaces since they're not imported
interface ApiResponse<T> {
  data: T;
  message?: string;
  status?: number;
}

interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

export interface WishlistRequest {
  productId: number;
  notes?: string;
  priority?: number;
}

export interface WishlistItem {
  id: number;
  productId: number;
  productName: string;
  productDescription: string;
  productPrice: string;
  productImage: string | null;
  sellerName: string;
  notes: string | null;
  priority: number;
  createdAt: string;
}

export const wishlistApi = {
  // Add product to wishlist
  addToWishlist: async (data: WishlistRequest): Promise<ApiResponse<WishlistItem>> => {
    return await api.post('/wishlist', data);
  },

  // Remove product from wishlist
  removeFromWishlist: async (productId: number): Promise<ApiResponse<void>> => {
    return await api.delete(`/wishlist/${productId}`);
  },

  // Get user's wishlist
  getWishlist: async (page = 0, size = 12): Promise<ApiResponse<PaginatedResponse<WishlistItem>>> => {
    return await api.get(`/wishlist?page=${page}&size=${size}`);
  },

  // Check if product is in wishlist
  isProductInWishlist: async (productId: number): Promise<ApiResponse<boolean>> => {
    return await api.get(`/wishlist/check/${productId}`);
  },

  // Get all wishlist product IDs
  getWishlistProductIds: async (): Promise<ApiResponse<number[]>> => {
    return await api.get('/wishlist/product-ids');
  },

  // Get wishlist count
  getWishlistCount: async (): Promise<ApiResponse<number>> => {
    return await api.get('/wishlist/count');
  },

  // Update wishlist item
  updateWishlistItem: async (productId: number, data: Partial<WishlistRequest>): Promise<ApiResponse<void>> => {
    return await api.put(`/wishlist/${productId}`, data);
  },

  // Get product wishlist count (how many users have wishlisted this product)
  getProductWishlistCount: async (productId: number): Promise<ApiResponse<number>> => {
    return await api.get(`/wishlist/product/${productId}/count`);
  },
};