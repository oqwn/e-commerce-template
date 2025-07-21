import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api } from '@/services/api';
import {
  CurrencyDollarIcon,
  ShoppingBagIcon,
  UserGroupIcon,
  ChartBarIcon,
  ArrowUpIcon,
  EyeIcon,
  PlusIcon,
  BuildingStorefrontIcon,
} from '@heroicons/react/24/outline';

interface DashboardStats {
  totalRevenue: number;
  totalProducts: number;
  totalOrders: number;
  totalCustomers: number;
  revenueChange: number;
  ordersChange: number;
  newOrders: number;
  pendingOrders: number;
}

const SellerDashboard: React.FC = () => {
  const navigate = useNavigate();
  const [hasStore, setHasStore] = useState<boolean | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  
  useEffect(() => {
    checkStore();
  }, []);

  const checkStore = async () => {
    try {
      const response = await api.get('/stores/my-store');
      if (response.data) {
        setHasStore(true);
      } else {
        setHasStore(false);
      }
    } catch (error) {
      setHasStore(false);
    } finally {
      setIsLoading(false);
    }
  };

  const [stats] = useState<DashboardStats>({
    totalRevenue: 12450.50,
    totalProducts: 24,
    totalOrders: 89,
    totalCustomers: 156,
    revenueChange: 12.3,
    ordersChange: 8.1,
    newOrders: 5,
    pendingOrders: 3,
  });

  const [recentOrders] = useState([
    { id: '1001', customer: 'John Doe', product: 'MacBook Pro 16-inch', amount: 2499.00, status: 'Confirmed' },
    { id: '1002', customer: 'Jane Smith', product: 'Dell XPS 15', amount: 1899.00, status: 'Pending' },
    { id: '1003', customer: 'Mike Johnson', product: 'Gaming Headset', amount: 89.99, status: 'Shipped' },
  ]);

  const [topProducts] = useState([
    { name: 'MacBook Pro 16-inch', sold: 15, revenue: 37485.00 },
    { name: 'Dell XPS 15', sold: 12, revenue: 22788.00 },
    { name: 'Gaming Headset', sold: 28, revenue: 2519.72 },
    { name: 'Wireless Mouse', sold: 45, revenue: 1349.55 },
  ]);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600"></div>
      </div>
    );
  }

  // If no store, show store registration prompt
  if (hasStore === false) {
    return (
      <div className="max-w-2xl mx-auto">
        <div className="bg-white rounded-lg shadow-sm border p-8 text-center">
          <BuildingStorefrontIcon className="h-16 w-16 text-green-600 mx-auto mb-4" />
          <h2 className="text-2xl font-bold text-gray-900 mb-2">Create Your Store</h2>
          <p className="text-gray-600 mb-6">
            You need to set up your store before you can start selling products.
          </p>
          <button
            onClick={() => navigate('/seller/store/register')}
            className="inline-flex items-center px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
          >
            <BuildingStorefrontIcon className="h-5 w-5 mr-2" />
            Create Store Now
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Welcome Section */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Welcome back!</h1>
            <p className="text-gray-600 mt-1">Here's what's happening with your store today.</p>
          </div>
          <div className="flex space-x-3">
            <Link
              to="/seller/store"
              className="inline-flex items-center px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
            >
              <BuildingStorefrontIcon className="h-5 w-5 mr-2" />
              Manage Store
            </Link>
            <Link
              to="/seller/products/new"
              className="inline-flex items-center px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
            >
              <PlusIcon className="h-5 w-5 mr-2" />
              Add Product
            </Link>
          </div>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Total Revenue</p>
              <p className="text-2xl font-bold text-gray-900">${stats.totalRevenue.toLocaleString()}</p>
            </div>
            <div className="bg-green-100 p-3 rounded-full">
              <CurrencyDollarIcon className="h-6 w-6 text-green-600" />
            </div>
          </div>
          <div className="mt-4 flex items-center">
            <ArrowUpIcon className="h-4 w-4 text-green-500" />
            <span className="text-sm text-green-500 ml-1">+{stats.revenueChange}%</span>
            <span className="text-sm text-gray-500 ml-2">from last month</span>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Total Products</p>
              <p className="text-2xl font-bold text-gray-900">{stats.totalProducts}</p>
            </div>
            <div className="bg-blue-100 p-3 rounded-full">
              <ShoppingBagIcon className="h-6 w-6 text-blue-600" />
            </div>
          </div>
          <div className="mt-4">
            <Link to="/seller/products" className="text-sm text-blue-600 hover:text-blue-700">
              Manage products →
            </Link>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Total Orders</p>
              <p className="text-2xl font-bold text-gray-900">{stats.totalOrders}</p>
            </div>
            <div className="bg-orange-100 p-3 rounded-full">
              <ChartBarIcon className="h-6 w-6 text-orange-600" />
            </div>
          </div>
          <div className="mt-4 flex items-center">
            <ArrowUpIcon className="h-4 w-4 text-green-500" />
            <span className="text-sm text-green-500 ml-1">+{stats.ordersChange}%</span>
            <span className="text-sm text-gray-500 ml-2">from last week</span>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Total Customers</p>
              <p className="text-2xl font-bold text-gray-900">{stats.totalCustomers}</p>
            </div>
            <div className="bg-purple-100 p-3 rounded-full">
              <UserGroupIcon className="h-6 w-6 text-purple-600" />
            </div>
          </div>
          <div className="mt-4">
            <span className="text-sm text-gray-500">Active customers</span>
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <Link
            to="/seller/orders"
            className="p-4 border border-orange-200 rounded-lg hover:bg-orange-50 transition-colors"
          >
            <div className="text-center">
              <div className="text-2xl font-bold text-orange-600">{stats.newOrders}</div>
              <div className="text-sm text-gray-600">New Orders</div>
            </div>
          </Link>
          <Link
            to="/seller/orders?status=pending"
            className="p-4 border border-yellow-200 rounded-lg hover:bg-yellow-50 transition-colors"
          >
            <div className="text-center">
              <div className="text-2xl font-bold text-yellow-600">{stats.pendingOrders}</div>
              <div className="text-sm text-gray-600">Pending Orders</div>
            </div>
          </Link>
          <Link
            to="/seller/products/new"
            className="p-4 border border-green-200 rounded-lg hover:bg-green-50 transition-colors"
          >
            <div className="text-center">
              <PlusIcon className="h-8 w-8 text-green-600 mx-auto mb-1" />
              <div className="text-sm text-gray-600">Add Product</div>
            </div>
          </Link>
          <Link
            to="/seller/analytics"
            className="p-4 border border-blue-200 rounded-lg hover:bg-blue-50 transition-colors"
          >
            <div className="text-center">
              <EyeIcon className="h-8 w-8 text-blue-600 mx-auto mb-1" />
              <div className="text-sm text-gray-600">View Analytics</div>
            </div>
          </Link>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Orders */}
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">Recent Orders</h2>
            <Link to="/seller/orders" className="text-sm text-blue-600 hover:text-blue-700">
              View all →
            </Link>
          </div>
          <div className="space-y-4">
            {recentOrders.map((order) => (
              <div key={order.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                <div>
                  <p className="font-medium text-gray-900">#{order.id}</p>
                  <p className="text-sm text-gray-600">{order.customer}</p>
                  <p className="text-sm text-gray-500">{order.product}</p>
                </div>
                <div className="text-right">
                  <p className="font-bold text-gray-900">${order.amount}</p>
                  <span className={`inline-block px-2 py-1 text-xs rounded-full ${
                    order.status === 'Confirmed' ? 'bg-green-100 text-green-800' :
                    order.status === 'Pending' ? 'bg-yellow-100 text-yellow-800' :
                    'bg-blue-100 text-blue-800'
                  }`}>
                    {order.status}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Top Products */}
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">Top Products</h2>
            <Link to="/seller/analytics" className="text-sm text-blue-600 hover:text-blue-700">
              View details →
            </Link>
          </div>
          <div className="space-y-4">
            {topProducts.map((product, index) => (
              <div key={index} className="flex items-center justify-between">
                <div className="flex-1">
                  <p className="font-medium text-gray-900">{product.name}</p>
                  <p className="text-sm text-gray-600">{product.sold} sold</p>
                </div>
                <div className="text-right">
                  <p className="font-bold text-gray-900">${product.revenue.toLocaleString()}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default SellerDashboard;