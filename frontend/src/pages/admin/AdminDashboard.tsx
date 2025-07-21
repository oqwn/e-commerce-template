import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import {
  UsersIcon,
  BuildingStorefrontIcon,
  ShoppingBagIcon,
  CurrencyDollarIcon,
  ExclamationTriangleIcon,
  ClockIcon,
} from '@heroicons/react/24/outline';

interface AdminStats {
  totalUsers: number;
  totalSellers: number;
  totalProducts: number;
  totalRevenue: number;
  pendingReviews: number;
  reportedContent: number;
  systemAlerts: number;
  activeUsers: number;
}

const AdminDashboard: React.FC = () => {
  const [stats] = useState<AdminStats>({
    totalUsers: 12450,
    totalSellers: 890,
    totalProducts: 15670,
    totalRevenue: 2450789.50,
    pendingReviews: 23,
    reportedContent: 5,
    systemAlerts: 2,
    activeUsers: 3456,
  });

  const [recentActivities] = useState([
    { type: 'user', action: 'New user registration', details: 'john.doe@example.com', time: '2 minutes ago' },
    { type: 'seller', action: 'Seller application', details: 'TechStore LLC pending review', time: '5 minutes ago' },
    { type: 'product', action: 'Product reported', details: 'Suspicious listing #12345', time: '10 minutes ago' },
    { type: 'order', action: 'High-value order', details: '$5,000 order placed', time: '15 minutes ago' },
    { type: 'review', action: 'Review flagged', details: 'Inappropriate content detected', time: '20 minutes ago' },
  ]);

  const [systemHealth] = useState({
    cpuUsage: 45,
    memoryUsage: 67,
    diskUsage: 23,
    apiResponseTime: '142ms',
    uptime: '99.98%',
  });

  return (
    <div className="space-y-6">
      {/* System Overview */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Platform Overview</h1>
            <p className="text-gray-600 mt-1">Monitor and manage your e-commerce platform</p>
          </div>
          <div className="flex items-center space-x-4">
            <div className="text-right">
              <p className="text-sm text-gray-500">System Status</p>
              <div className="flex items-center">
                <div className="w-3 h-3 bg-green-400 rounded-full animate-pulse mr-2"></div>
                <span className="text-sm font-medium text-green-600">All Systems Operational</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Key Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Total Users</p>
              <p className="text-2xl font-bold text-gray-900">{stats.totalUsers.toLocaleString()}</p>
            </div>
            <div className="bg-blue-100 p-3 rounded-full">
              <UsersIcon className="h-6 w-6 text-blue-600" />
            </div>
          </div>
          <div className="mt-4">
            <span className="text-sm text-gray-500">{stats.activeUsers.toLocaleString()} active today</span>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Total Sellers</p>
              <p className="text-2xl font-bold text-gray-900">{stats.totalSellers.toLocaleString()}</p>
            </div>
            <div className="bg-green-100 p-3 rounded-full">
              <BuildingStorefrontIcon className="h-6 w-6 text-green-600" />
            </div>
          </div>
          <div className="mt-4">
            <Link to="/admin/sellers" className="text-sm text-green-600 hover:text-green-700">
              Manage sellers →
            </Link>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Total Products</p>
              <p className="text-2xl font-bold text-gray-900">{stats.totalProducts.toLocaleString()}</p>
            </div>
            <div className="bg-purple-100 p-3 rounded-full">
              <ShoppingBagIcon className="h-6 w-6 text-purple-600" />
            </div>
          </div>
          <div className="mt-4">
            <Link to="/admin/products" className="text-sm text-purple-600 hover:text-purple-700">
              Review products →
            </Link>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Platform Revenue</p>
              <p className="text-2xl font-bold text-gray-900">${(stats.totalRevenue / 1000000).toFixed(1)}M</p>
            </div>
            <div className="bg-yellow-100 p-3 rounded-full">
              <CurrencyDollarIcon className="h-6 w-6 text-yellow-600" />
            </div>
          </div>
          <div className="mt-4">
            <Link to="/admin/reports" className="text-sm text-yellow-600 hover:text-yellow-700">
              View reports →
            </Link>
          </div>
        </div>
      </div>

      {/* Action Required */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Action Required</h2>
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <Link
            to="/admin/moderation"
            className="p-4 border border-red-200 rounded-lg hover:bg-red-50 transition-colors"
          >
            <div className="flex items-center">
              <ExclamationTriangleIcon className="h-8 w-8 text-red-600" />
              <div className="ml-3">
                <div className="text-lg font-bold text-red-600">{stats.pendingReviews}</div>
                <div className="text-sm text-gray-600">Pending Reviews</div>
              </div>
            </div>
          </Link>

          <Link
            to="/admin/reports"
            className="p-4 border border-orange-200 rounded-lg hover:bg-orange-50 transition-colors"
          >
            <div className="flex items-center">
              <ClockIcon className="h-8 w-8 text-orange-600" />
              <div className="ml-3">
                <div className="text-lg font-bold text-orange-600">{stats.reportedContent}</div>
                <div className="text-sm text-gray-600">Reported Content</div>
              </div>
            </div>
          </Link>

          <Link
            to="/admin/system-health"
            className="p-4 border border-yellow-200 rounded-lg hover:bg-yellow-50 transition-colors"
          >
            <div className="flex items-center">
              <ExclamationTriangleIcon className="h-8 w-8 text-yellow-600" />
              <div className="ml-3">
                <div className="text-lg font-bold text-yellow-600">{stats.systemAlerts}</div>
                <div className="text-sm text-gray-600">System Alerts</div>
              </div>
            </div>
          </Link>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Activities */}
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">Recent Activities</h2>
            <Link to="/admin/audit-logs" className="text-sm text-blue-600 hover:text-blue-700">
              View all →
            </Link>
          </div>
          <div className="space-y-4">
            {recentActivities.map((activity, index) => (
              <div key={index} className="flex items-start space-x-3">
                <div className={`w-8 h-8 rounded-full flex items-center justify-center ${
                  activity.type === 'user' ? 'bg-blue-100' :
                  activity.type === 'seller' ? 'bg-green-100' :
                  activity.type === 'product' ? 'bg-purple-100' :
                  activity.type === 'order' ? 'bg-yellow-100' :
                  'bg-red-100'
                }`}>
                  {activity.type === 'user' && <UsersIcon className="h-4 w-4 text-blue-600" />}
                  {activity.type === 'seller' && <BuildingStorefrontIcon className="h-4 w-4 text-green-600" />}
                  {activity.type === 'product' && <ShoppingBagIcon className="h-4 w-4 text-purple-600" />}
                  {activity.type === 'order' && <CurrencyDollarIcon className="h-4 w-4 text-yellow-600" />}
                  {activity.type === 'review' && <ExclamationTriangleIcon className="h-4 w-4 text-red-600" />}
                </div>
                <div className="flex-1">
                  <p className="text-sm font-medium text-gray-900">{activity.action}</p>
                  <p className="text-sm text-gray-600">{activity.details}</p>
                  <p className="text-xs text-gray-500 mt-1">{activity.time}</p>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* System Health */}
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">System Health</h2>
            <Link to="/admin/system-health" className="text-sm text-blue-600 hover:text-blue-700">
              View details →
            </Link>
          </div>
          <div className="space-y-4">
            <div>
              <div className="flex justify-between items-center mb-2">
                <span className="text-sm font-medium text-gray-600">CPU Usage</span>
                <span className="text-sm text-gray-900">{systemHealth.cpuUsage}%</span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div
                  className="bg-blue-600 h-2 rounded-full"
                  style={{ width: `${systemHealth.cpuUsage}%` }}
                ></div>
              </div>
            </div>

            <div>
              <div className="flex justify-between items-center mb-2">
                <span className="text-sm font-medium text-gray-600">Memory Usage</span>
                <span className="text-sm text-gray-900">{systemHealth.memoryUsage}%</span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div
                  className="bg-yellow-600 h-2 rounded-full"
                  style={{ width: `${systemHealth.memoryUsage}%` }}
                ></div>
              </div>
            </div>

            <div>
              <div className="flex justify-between items-center mb-2">
                <span className="text-sm font-medium text-gray-600">Disk Usage</span>
                <span className="text-sm text-gray-900">{systemHealth.diskUsage}%</span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div
                  className="bg-green-600 h-2 rounded-full"
                  style={{ width: `${systemHealth.diskUsage}%` }}
                ></div>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4 pt-4 border-t border-gray-200">
              <div>
                <p className="text-sm font-medium text-gray-600">API Response</p>
                <p className="text-lg font-bold text-gray-900">{systemHealth.apiResponseTime}</p>
              </div>
              <div>
                <p className="text-sm font-medium text-gray-600">Uptime</p>
                <p className="text-lg font-bold text-green-600">{systemHealth.uptime}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;