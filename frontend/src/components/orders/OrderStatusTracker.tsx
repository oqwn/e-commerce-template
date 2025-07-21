import React from 'react';
import {
  ClockIcon,
  TruckIcon,
  HomeIcon,
  XCircleIcon
} from '@heroicons/react/24/outline';
import { CheckCircleIcon as CheckCircleIconSolid } from '@heroicons/react/24/solid';

interface OrderStatusTrackerProps {
  status: string;
  createdAt: string;
  estimatedDeliveryDate?: string;
  deliveredAt?: string;
  cancelledAt?: string;
}

const OrderStatusTracker: React.FC<OrderStatusTrackerProps> = ({
  status,
  createdAt,
  estimatedDeliveryDate,
  deliveredAt,
  cancelledAt
}) => {
  const getOrderSteps = () => {
    const isCancelled = status === 'CANCELLED' || status === 'REFUNDED';
    
    if (isCancelled) {
      return [
        {
          name: 'Order Placed',
          description: 'Order was placed',
          icon: CheckCircleIconSolid,
          status: 'completed',
          date: createdAt
        },
        {
          name: status === 'REFUNDED' ? 'Refunded' : 'Cancelled',
          description: status === 'REFUNDED' ? 'Order was refunded' : 'Order was cancelled',
          icon: XCircleIcon,
          status: 'completed',
          date: cancelledAt || createdAt
        }
      ];
    }

    const steps = [
      {
        name: 'Order Placed',
        description: 'Order was placed and is being processed',
        icon: CheckCircleIconSolid,
        status: 'completed',
        date: createdAt
      },
      {
        name: 'Confirmed',
        description: 'Payment confirmed and order is being prepared',
        icon: ClockIcon,
        status: ['PENDING'].includes(status) ? 'current' : 'completed',
        date: null
      },
      {
        name: 'Processing',
        description: 'Order is being prepared for shipment',
        icon: ClockIcon,
        status: ['PENDING', 'CONFIRMED'].includes(status) ? 'pending' : 
               ['PROCESSING'].includes(status) ? 'current' : 'completed',
        date: null
      },
      {
        name: 'Shipped',
        description: 'Order has been shipped',
        icon: TruckIcon,
        status: ['PENDING', 'CONFIRMED', 'PROCESSING'].includes(status) ? 'pending' :
               ['SHIPPED'].includes(status) ? 'current' : 'completed',
        date: null
      },
      {
        name: 'Delivered',
        description: 'Order has been delivered',
        icon: HomeIcon,
        status: ['DELIVERED'].includes(status) ? 'completed' : 'pending',
        date: deliveredAt || null
      }
    ];

    return steps;
  };

  const steps = getOrderSteps();

  const getStepStyles = (stepStatus: string) => {
    switch (stepStatus) {
      case 'completed':
        return {
          container: 'bg-green-50 border-green-200',
          icon: 'text-green-600 bg-green-100',
          text: 'text-green-800',
          line: 'bg-green-200'
        };
      case 'current':
        return {
          container: 'bg-blue-50 border-blue-200',
          icon: 'text-blue-600 bg-blue-100',
          text: 'text-blue-800',
          line: 'bg-gray-200'
        };
      case 'pending':
      default:
        return {
          container: 'bg-gray-50 border-gray-200',
          icon: 'text-gray-400 bg-gray-100',
          text: 'text-gray-600',
          line: 'bg-gray-200'
        };
    }
  };

  const formatDate = (dateString: string | null) => {
    if (!dateString) return null;
    return new Date(dateString).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  return (
    <div className="bg-white rounded-lg border p-6">
      <h3 className="text-lg font-medium text-gray-900 mb-6">Order Status</h3>
      
      <div className="relative">
        {steps.map((step, stepIdx) => {
          const styles = getStepStyles(step.status);
          const IconComponent = step.icon;
          
          return (
            <div key={step.name} className="relative">
              {stepIdx !== steps.length - 1 && (
                <div
                  className={`absolute left-4 top-8 h-full w-0.5 ${styles.line}`}
                  aria-hidden="true"
                />
              )}
              
              <div className="relative flex items-start">
                <div className={`flex h-8 w-8 items-center justify-center rounded-full border-2 ${styles.container}`}>
                  <IconComponent className={`h-4 w-4 ${styles.icon}`} />
                </div>
                
                <div className="ml-4 min-w-0 flex-1">
                  <div className="flex items-center justify-between">
                    <p className={`text-sm font-medium ${styles.text}`}>
                      {step.name}
                    </p>
                    {step.date && (
                      <p className="text-sm text-gray-500">
                        {formatDate(step.date)}
                      </p>
                    )}
                  </div>
                  <p className="text-sm text-gray-600 mt-1">
                    {step.description}
                  </p>
                </div>
              </div>
              
              {stepIdx !== steps.length - 1 && (
                <div className="pb-6" />
              )}
            </div>
          );
        })}
      </div>

      {estimatedDeliveryDate && status !== 'DELIVERED' && status !== 'CANCELLED' && status !== 'REFUNDED' && (
        <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-md">
          <p className="text-sm text-blue-800">
            <TruckIcon className="inline h-4 w-4 mr-1" />
            Estimated delivery: {formatDate(estimatedDeliveryDate)}
          </p>
        </div>
      )}
    </div>
  );
};

export default OrderStatusTracker;