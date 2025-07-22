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
    api.post('/flash-sales', data),

  // Update flash sale
  updateFlashSale: (id: number, data: UpdateFlashSaleRequest) =>
    api.put(`/flash-sales/${id}`, data),

  // Delete flash sale
  deleteFlashSale: (id: number) =>
    api.delete(`/flash-sales/${id}`),

  // Get flash sale by ID
  getFlashSale: (id: number) =>
    api.get(`/flash-sales/${id}`),

  // Get active flash sales
  getActiveFlashSales: () =>
    api.get('/flash-sales/active'),

  // Get upcoming flash sales
  getUpcomingFlashSales: () =>
    api.get('/flash-sales/upcoming'),

  // Get user's flash sales
  getMyFlashSales: () =>
    api.get('/flash-sales/my-flash-sales'),

  // Get all flash sales (admin)
  getAllFlashSales: () =>
    api.get('/flash-sales'),

  // Get active flash sale products
  getActiveFlashSaleProducts: () =>
    api.get('/flash-sales/products'),

  // Get flash sale by product ID
  getFlashSaleByProduct: (productId: number) =>
    api.get(`/flash-sales/products/${productId}`),

  // Activate flash sale
  activateFlashSale: (id: number) =>
    api.put(`/flash-sales/${id}/activate`, {}),

  // Deactivate flash sale
  deactivateFlashSale: (id: number) =>
    api.put(`/flash-sales/${id}/deactivate`, {}),

  // Record purchase (internal use)
  recordFlashSalePurchase: (productId: number) =>
    api.post(`/flash-sales/products/${productId}/purchase`, {}),
};