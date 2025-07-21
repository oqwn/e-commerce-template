import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { 
  CheckCircleIcon, 
  TruckIcon, 
  MapPinIcon, 
  CreditCardIcon, 
  PrinterIcon, 
  ArrowLeftIcon,
  DocumentDuplicateIcon
} from '@heroicons/react/24/outline';
import { Order } from '@/types';
import orderService from '@/services/orderService';
import OrderStatusTracker from '@/components/orders/OrderStatusTracker';
import { formatCurrency, formatDateTime } from '@/utils/formatters';
import { toast } from 'react-toastify';

const OrderConfirmation: React.FC = () => {
  const { orderId } = useParams<{ orderId: string }>();
  const navigate = useNavigate();
  const [order, setOrder] = useState<Order | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!orderId) {
      navigate('/');
      return;
    }
    fetchOrderDetails();
  }, [orderId, navigate]);

  const fetchOrderDetails = async () => {
    try {
      const orderData = await orderService.getOrderById(parseInt(orderId!));
      setOrder(orderData);
    } catch (error) {
      console.error('Failed to fetch order details:', error);
      toast.error('Failed to load order details');
      navigate('/buyer/orders');
    } finally {
      setLoading(false);
    }
  };

  const copyOrderNumber = () => {
    if (order) {
      navigator.clipboard.writeText(order.orderNumber);
      toast.success('Order number copied to clipboard');
    }
  };

  const handlePrint = () => {
    window.print();
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!order) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Order Not Found</h2>
          <p className="text-gray-600 mb-6">The order you're looking for doesn't exist or you don't have permission to view it.</p>
          <Link
            to="/buyer/orders"
            className="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
          >
            <ArrowLeftIcon className="h-4 w-4 mr-2" />
            Back to Orders
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-4">
              <Link
                to="/buyer/orders"
                className="inline-flex items-center text-sm font-medium text-gray-600 hover:text-gray-900"
              >
                <ArrowLeftIcon className="h-4 w-4 mr-2" />
                Back to Orders
              </Link>
            </div>
            
            <div className="flex items-center space-x-3">
              <button
                onClick={handlePrint}
                className="inline-flex items-center px-3 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
              >
                <PrinterIcon className="h-4 w-4 mr-2" />
                Print
              </button>
            </div>
          </div>
        </div>

        {/* Order Success Banner */}
        <div className="bg-green-50 border border-green-200 rounded-lg p-6 mb-8">
          <div className="flex items-center">
            <CheckCircleIcon className="h-8 w-8 text-green-500 mr-4" />
            <div>
              <h1 className="text-2xl font-bold text-green-800 mb-1">
                Order Confirmed!
              </h1>
              <p className="text-green-700">
                Thank you for your purchase. Your order has been successfully placed.
              </p>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-2 space-y-8">
            {/* Order Details */}
            <div className="bg-white rounded-lg shadow-sm border p-6">
              <div className="flex items-center justify-between mb-6">
                <h2 className="text-lg font-medium text-gray-900">Order Details</h2>
                <div className="flex items-center space-x-2">
                  <button
                    onClick={copyOrderNumber}
                    className="inline-flex items-center text-sm text-gray-600 hover:text-gray-900"
                  >
                    <DocumentDuplicateIcon className="h-4 w-4 mr-1" />
                    Copy Order #
                  </button>
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 mb-6">
                <div>
                  <p className="text-sm font-medium text-gray-500">Order Number</p>
                  <p className="text-lg font-medium text-gray-900">{order.orderNumber}</p>
                </div>
                <div>
                  <p className="text-sm font-medium text-gray-500">Order Date</p>
                  <p className="text-lg text-gray-900">{formatDateTime(order.createdAt)}</p>
                </div>
                <div>
                  <p className="text-sm font-medium text-gray-500">Total Amount</p>
                  <p className="text-lg font-bold text-gray-900">{formatCurrency(order.totalAmount)}</p>
                </div>
                <div>
                  <p className="text-sm font-medium text-gray-500">Payment Status</p>
                  <span className={`inline-flex px-2 py-1 text-xs font-medium rounded-full ${
                    order.paymentStatus === 'COMPLETED'
                      ? 'bg-green-100 text-green-800'
                      : order.paymentStatus === 'PENDING'
                      ? 'bg-yellow-100 text-yellow-800'
                      : 'bg-red-100 text-red-800'
                  }`}>
                    {order.paymentStatus}
                  </span>
                </div>
              </div>

              {order.estimatedDeliveryDate && (
                <div className="border-t pt-6">
                  <div className="flex items-center text-blue-600">
                    <TruckIcon className="h-5 w-5 mr-2" />
                    <span className="text-sm font-medium">
                      Estimated delivery: {formatDateTime(order.estimatedDeliveryDate)}
                    </span>
                  </div>
                </div>
              )}
            </div>

            {/* Order Items */}
            <div className="bg-white rounded-lg shadow-sm border p-6">
              <h2 className="text-lg font-medium text-gray-900 mb-6">Order Items</h2>
              <div className="space-y-4">
                {order.items.map((item) => (
                  <div key={item.id} className="flex items-center space-x-4 py-4 border-b border-gray-200 last:border-b-0">
                    {item.productImageUrl && (
                      <img
                        src={item.productImageUrl}
                        alt={item.productName}
                        className="w-16 h-16 object-cover rounded-md"
                      />
                    )}
                    <div className="flex-1 min-w-0">
                      <h3 className="text-sm font-medium text-gray-900">
                        {item.productName}
                      </h3>
                      <p className="text-sm text-gray-600">
                        SKU: {item.productSku}
                      </p>
                      {item.variantOptions && (
                        <p className="text-sm text-gray-600">
                          Variant: {item.variantOptions}
                        </p>
                      )}
                    </div>
                    <div className="text-right">
                      <p className="text-sm text-gray-900">
                        {item.quantity} × {formatCurrency(item.unitPrice)}
                      </p>
                      <p className="text-sm font-medium text-gray-900">
                        {formatCurrency(item.totalPrice)}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Shipping Address */}
            <div className="bg-white rounded-lg shadow-sm border p-6">
              <div className="flex items-center mb-4">
                <MapPinIcon className="h-5 w-5 text-gray-400 mr-2" />
                <h2 className="text-lg font-medium text-gray-900">Shipping Address</h2>
              </div>
              <div className="text-sm text-gray-900">
                <p className="font-medium">{order.shippingAddress.firstName} {order.shippingAddress.lastName}</p>
                {order.shippingAddress.phone && (
                  <p>{order.shippingAddress.phone}</p>
                )}
                <p>{order.shippingAddress.addressLine1}</p>
                {order.shippingAddress.addressLine2 && (
                  <p>{order.shippingAddress.addressLine2}</p>
                )}
                <p>
                  {order.shippingAddress.city}, {order.shippingAddress.state} {order.shippingAddress.postalCode}
                </p>
                <p>{order.shippingAddress.country}</p>
              </div>
            </div>

            {/* Payment Method */}
            <div className="bg-white rounded-lg shadow-sm border p-6">
              <div className="flex items-center mb-4">
                <CreditCardIcon className="h-5 w-5 text-gray-400 mr-2" />
                <h2 className="text-lg font-medium text-gray-900">Payment Method</h2>
              </div>
              <p className="text-sm text-gray-900">{order.paymentMethod}</p>
            </div>
          </div>

          {/* Sidebar */}
          <div className="space-y-8">
            {/* Order Status Tracker */}
            <OrderStatusTracker
              status={order.status}
              createdAt={order.createdAt}
              estimatedDeliveryDate={order.estimatedDeliveryDate}
              deliveredAt={order.deliveredAt}
              cancelledAt={order.cancelledAt}
            />

            {/* Order Summary */}
            <div className="bg-white rounded-lg shadow-sm border p-6">
              <h3 className="text-lg font-medium text-gray-900 mb-4">Order Summary</h3>
              <div className="space-y-3">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Subtotal ({order.totalItems} items)</span>
                  <span className="text-gray-900">{formatCurrency(order.subtotalAmount)}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Shipping</span>
                  <span className="text-gray-900">{formatCurrency(order.shippingAmount)}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Tax</span>
                  <span className="text-gray-900">{formatCurrency(order.taxAmount)}</span>
                </div>
                {order.discountAmount && order.discountAmount > 0 && (
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600">Discount</span>
                    <span className="text-green-600">-{formatCurrency(order.discountAmount)}</span>
                  </div>
                )}
                <div className="border-t pt-3 flex justify-between">
                  <span className="text-base font-medium text-gray-900">Total</span>
                  <span className="text-lg font-bold text-gray-900">{formatCurrency(order.totalAmount)}</span>
                </div>
              </div>
            </div>

            {/* Actions */}
            <div className="bg-white rounded-lg shadow-sm border p-6">
              <h3 className="text-lg font-medium text-gray-900 mb-4">Need Help?</h3>
              <div className="space-y-3">
                <Link
                  to="/buyer/orders"
                  className="block w-full text-center px-4 py-2 border border-gray-300 text-sm font-medium text-gray-700 bg-white rounded-md hover:bg-gray-50"
                >
                  View All Orders
                </Link>
                <Link
                  to="/support"
                  className="block w-full text-center px-4 py-2 border border-gray-300 text-sm font-medium text-gray-700 bg-white rounded-md hover:bg-gray-50"
                >
                  Contact Support
                </Link>
                <Link
                  to="/buyer/products"
                  className="block w-full text-center px-4 py-2 bg-blue-600 text-sm font-medium text-white rounded-md hover:bg-blue-700"
                >
                  Continue Shopping
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default OrderConfirmation;