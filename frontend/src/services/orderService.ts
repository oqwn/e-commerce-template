import api from './api';
import { Order } from '@/types';

export interface OrdersResponse {
  success: boolean;
  message: string;
  data: Order[];
}

export interface OrderResponse {
  success: boolean;
  message: string;
  data: Order;
}

class OrderService {
  /**
   * Get user's order history with pagination
   */
  async getUserOrders(page: number = 0, size: number = 10): Promise<Order[]> {
    const response = await api.get(`/orders/my-orders?page=${page}&size=${size}`);
    return response.data.data || [];
  }

  /**
   * Get seller's store orders with pagination and filtering
   */
  async getSellerOrders(page: number = 0, size: number = 10, status?: string, search?: string): Promise<Order[]> {
    let url = `/orders/my-store-orders?page=${page}&size=${size}`;
    if (status && status !== 'all') {
      url += `&status=${status}`;
    }
    if (search) {
      url += `&search=${search}`;
    }
    const response = await api.get(url);
    return response.data.data || [];
  }

  /**
   * Update order status (seller only)
   */
  async updateOrderStatus(orderId: number, status: string, notes?: string): Promise<void> {
    await api.put(`/orders/${orderId}/status`, { status, notes });
  }

  /**
   * Get orders count by status for seller
   */
  async getOrdersStats(): Promise<{ [key: string]: number }> {
    const response = await api.get('/orders/my-store-stats');
    return response.data.data || {};
  }

  /**
   * Get order by ID
   */
  async getOrderById(orderId: number): Promise<Order> {
    const response = await api.get(`/orders/${orderId}`);
    return response.data.data;
  }

  /**
   * Get order by order number
   */
  async getOrderByNumber(orderNumber: string): Promise<Order> {
    const response = await api.get(`/orders/number/${orderNumber}`);
    return response.data.data;
  }

  /**
   * Cancel an order (if allowed)
   */
  async cancelOrder(orderId: number, reason?: string): Promise<void> {
    await api.post(`/orders/${orderId}/cancel`, { reason });
  }

  /**
   * Track order - get detailed order with tracking info
   */
  async trackOrder(orderNumber: string): Promise<Order> {
    return this.getOrderByNumber(orderNumber);
  }
}

export default new OrderService();