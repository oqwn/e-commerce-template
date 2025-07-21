import api from './api';

export interface PaymentIntentResponse {
  clientSecret: string;
  paymentIntentId: string;
  amount: number;
  currency: string;
  status: string;
}

export interface CreatePaymentRequest {
  orderId: number;
}

export interface RefundRequest {
  orderId: number;
  amount: number;
  reason?: string;
}

class PaymentService {
  /**
   * Create a payment intent for an order
   */
  async createPaymentIntent(orderId: number): Promise<PaymentIntentResponse> {
    const response = await api.post('/payments/create-intent', {
      orderId
    });
    return response.data;
  }

  /**
   * Get payment details for an order
   */
  async getPaymentByOrderId(orderId: number) {
    const response = await api.get(`/payments/order/${orderId}`);
    return response.data;
  }

  /**
   * Process a refund (admin/seller only)
   */
  async processRefund(request: RefundRequest) {
    const response = await api.post('/payments/refund', request);
    return response.data;
  }
}

export default new PaymentService();