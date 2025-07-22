import { api } from '@/services/api';

export interface CreateFlashSaleRequest {
  name: string;
  description?: string;
  startTime: string;
  endTime: string;
  discountPercentage: number;
  maxQuantity?: number;
  products: FlashSaleProductRequest[];
}

export interface FlashSaleProductRequest {
  productId: number;
  originalPrice: number;
  salePrice: number;
  maxQuantityPerProduct?: number;
}

export interface UpdateFlashSaleRequest {
  name?: string;
  description?: string;
  startTime?: string;
  endTime?: string;
  discountPercentage?: number;
  maxQuantity?: number;
  isActive?: boolean;
}

export interface FlashSale {
  id: number;
  name: string;
  description: string;
  startTime: string;
  endTime: string;
  discountPercentage: number;
  maxQuantity: number | null;
  usedQuantity: number;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
  createdBy: number;
  creatorName: string;
  currentlyActive: boolean;
  upcoming: boolean;
  expired: boolean;
  hasQuantityAvailable: boolean;
  remainingQuantity: number;
  products: FlashSaleProduct[];
}

export interface FlashSaleProduct {
  id: number;
  flashSaleId: number;
  productId: number;
  originalPrice: number;
  salePrice: number;
  maxQuantityPerProduct: number | null;
  usedQuantityPerProduct: number;
  createdAt: string;
  productName: string;
  productSlug: string;
  productImage: string | null;
  sellerName: string;
  flashSaleName?: string;
  flashSaleStartTime?: string;
  flashSaleEndTime?: string;
  hasQuantityAvailable: boolean;
  remainingQuantity: number;
  discountAmount: number;
  discountPercentage: number;
}

export const flashSalesApi = {
  // Create flash sale
  createFlashSale: (data: CreateFlashSaleRequest) =>
    api.post('/api/flash-sales', data),

  // Update flash sale
  updateFlashSale: (id: number, data: UpdateFlashSaleRequest) =>
    api.put(`/api/flash-sales/${id}`, data),

  // Delete flash sale
  deleteFlashSale: (id: number) =>
    api.delete(`/api/flash-sales/${id}`),

  // Get flash sale by ID
  getFlashSale: (id: number) =>
    api.get(`/api/flash-sales/${id}`),

  // Get active flash sales
  getActiveFlashSales: () =>
    api.get('/api/flash-sales/active'),

  // Get upcoming flash sales
  getUpcomingFlashSales: () =>
    api.get('/api/flash-sales/upcoming'),

  // Get user's flash sales
  getMyFlashSales: () =>
    api.get('/api/flash-sales/my-flash-sales'),

  // Get all flash sales (admin)
  getAllFlashSales: () =>
    api.get('/api/flash-sales'),

  // Get active flash sale products
  getActiveFlashSaleProducts: () =>
    api.get('/api/flash-sales/products'),

  // Get flash sale by product ID
  getFlashSaleByProduct: (productId: number) =>
    api.get(`/api/flash-sales/products/${productId}`),

  // Activate flash sale
  activateFlashSale: (id: number) =>
    api.put(`/api/flash-sales/${id}/activate`, {}),

  // Deactivate flash sale
  deactivateFlashSale: (id: number) =>
    api.put(`/api/flash-sales/${id}/deactivate`, {}),

  // Record purchase (internal use)
  recordFlashSalePurchase: (productId: number) =>
    api.post(`/api/flash-sales/products/${productId}/purchase`, {}),
};