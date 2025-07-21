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