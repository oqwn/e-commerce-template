import React, { useState, useEffect } from 'react';
import { api } from '@/services/api';
import { StoreAnalytics as StoreAnalyticsType } from '@/types';
import {
  ChartBarIcon,
  CurrencyDollarIcon,
  ShoppingCartIcon,
  UserGroupIcon,
  EyeIcon,
  ArrowUpIcon,
  ArrowDownIcon,
  CalendarIcon,
} from '@heroicons/react/24/outline';
import {
  LineChart,
  Line,
  BarChart,
  Bar,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend,
} from 'recharts';

interface AnalyticsSummary {
  totalRevenue: number;
  totalOrders: number;
  avgOrderValue: number;
  conversionRate: number;
  revenueChange: number;
  ordersChange: number;
  conversionChange: number;
}

const StoreAnalytics: React.FC = () => {
  const [analytics, setAnalytics] = useState<StoreAnalyticsType[]>([]);
  const [summary, setSummary] = useState<AnalyticsSummary | null>(null);
  const [dateRange, setDateRange] = useState('last7days');
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    fetchAnalytics();
  }, [dateRange]);

  const fetchAnalytics = async () => {
    try {
      setIsLoading(true);
      const response = await api.get(`/stores/analytics?range=${dateRange}`);
      if (response.data.data) {
        setAnalytics(response.data.data.analytics || []);
        setSummary(response.data.data.summary);
      }
    } catch (error) {
      console.error('Error fetching analytics:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(value);
  };

  const formatPercentage = (value: number) => {
    return `${value.toFixed(2)}%`;
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600"></div>
      </div>
    );
  }

  // Prepare data for charts
  const revenueData = analytics.map(day => ({
    date: formatDate(day.date),
    revenue: day.totalRevenue,
    orders: day.totalOrders,
  }));

  const conversionData = analytics.map(day => ({
    date: formatDate(day.date),
    rate: day.conversionRate,
    visits: day.totalVisits,
  }));

  const productFunnelData = analytics.length > 0 ? [
    { name: 'Viewed', value: analytics[analytics.length - 1].productsViewed },
    { name: 'Added to Cart', value: analytics[analytics.length - 1].productsAddedToCart },
    { name: 'Purchased', value: analytics[analytics.length - 1].productsPurchased },
  ] : [];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Store Analytics</h1>
            <p className="text-gray-600 mt-1">Track your store's performance and insights</p>
          </div>
          <div className="flex items-center space-x-2">
            <CalendarIcon className="h-5 w-5 text-gray-400" />
            <select
              value={dateRange}
              onChange={(e) => setDateRange(e.target.value)}
              className="border-gray-300 rounded-md shadow-sm focus:ring-green-500 focus:border-green-500 sm:text-sm"
            >
              <option value="last7days">Last 7 days</option>
              <option value="last30days">Last 30 days</option>
              <option value="last90days">Last 90 days</option>
            </select>
          </div>
        </div>
      </div>

      {/* Summary Cards */}
      {summary && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <div className="bg-white rounded-lg shadow-sm border p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Total Revenue</p>
                <p className="text-2xl font-bold text-gray-900">
                  {formatCurrency(summary.totalRevenue)}
                </p>
              </div>
              <div className="bg-green-100 p-3 rounded-full">
                <CurrencyDollarIcon className="h-6 w-6 text-green-600" />
              </div>
            </div>
            <div className="mt-4 flex items-center">
              {summary.revenueChange >= 0 ? (
                <ArrowUpIcon className="h-4 w-4 text-green-500" />
              ) : (
                <ArrowDownIcon className="h-4 w-4 text-red-500" />
              )}
              <span className={`text-sm ml-1 ${
                summary.revenueChange >= 0 ? 'text-green-500' : 'text-red-500'
              }`}>
                {Math.abs(summary.revenueChange).toFixed(1)}%
              </span>
              <span className="text-sm text-gray-500 ml-2">vs previous period</span>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm border p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Total Orders</p>
                <p className="text-2xl font-bold text-gray-900">{summary.totalOrders}</p>
              </div>
              <div className="bg-blue-100 p-3 rounded-full">
                <ShoppingCartIcon className="h-6 w-6 text-blue-600" />
              </div>
            </div>
            <div className="mt-4 flex items-center">
              {summary.ordersChange >= 0 ? (
                <ArrowUpIcon className="h-4 w-4 text-green-500" />
              ) : (
                <ArrowDownIcon className="h-4 w-4 text-red-500" />
              )}
              <span className={`text-sm ml-1 ${
                summary.ordersChange >= 0 ? 'text-green-500' : 'text-red-500'
              }`}>
                {Math.abs(summary.ordersChange).toFixed(1)}%
              </span>
              <span className="text-sm text-gray-500 ml-2">vs previous period</span>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm border p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Avg Order Value</p>
                <p className="text-2xl font-bold text-gray-900">
                  {formatCurrency(summary.avgOrderValue)}
                </p>
              </div>
              <div className="bg-yellow-100 p-3 rounded-full">
                <ChartBarIcon className="h-6 w-6 text-yellow-600" />
              </div>
            </div>
            <div className="mt-4">
              <span className="text-sm text-gray-500">Per transaction</span>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm border p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Conversion Rate</p>
                <p className="text-2xl font-bold text-gray-900">
                  {formatPercentage(summary.conversionRate)}
                </p>
              </div>
              <div className="bg-purple-100 p-3 rounded-full">
                <UserGroupIcon className="h-6 w-6 text-purple-600" />
              </div>
            </div>
            <div className="mt-4 flex items-center">
              {summary.conversionChange >= 0 ? (
                <ArrowUpIcon className="h-4 w-4 text-green-500" />
              ) : (
                <ArrowDownIcon className="h-4 w-4 text-red-500" />
              )}
              <span className={`text-sm ml-1 ${
                summary.conversionChange >= 0 ? 'text-green-500' : 'text-red-500'
              }`}>
                {Math.abs(summary.conversionChange).toFixed(1)}%
              </span>
              <span className="text-sm text-gray-500 ml-2">vs previous period</span>
            </div>
          </div>
        </div>
      )}

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Revenue & Orders Chart */}
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Revenue & Orders</h3>
          <div className="h-80">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={revenueData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis yAxisId="left" />
                <YAxis yAxisId="right" orientation="right" />
                <Tooltip formatter={(value, name) => [
                  name === 'revenue' ? formatCurrency(Number(value)) : value,
                  name === 'revenue' ? 'Revenue' : 'Orders'
                ]} />
                <Legend />
                <Line
                  yAxisId="left"
                  type="monotone"
                  dataKey="revenue"
                  stroke="#10B981"
                  strokeWidth={2}
                  name="Revenue"
                />
                <Line
                  yAxisId="right"
                  type="monotone"
                  dataKey="orders"
                  stroke="#3B82F6"
                  strokeWidth={2}
                  name="Orders"
                />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Conversion Rate Chart */}
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Conversion Rate</h3>
          <div className="h-80">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={conversionData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis />
                <Tooltip formatter={(value) => formatPercentage(Number(value))} />
                <Area
                  type="monotone"
                  dataKey="rate"
                  stroke="#8B5CF6"
                  fill="#8B5CF6"
                  fillOpacity={0.3}
                  name="Conversion Rate"
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      {/* Product Funnel */}
      {productFunnelData.length > 0 && (
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Product Funnel</h3>
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div className="h-80">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={productFunnelData} layout="horizontal">
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="value" fill="#10B981" />
                </BarChart>
              </ResponsiveContainer>
            </div>
            <div className="space-y-4">
              <div className="p-4 bg-gray-50 rounded-lg">
                <div className="flex items-center justify-between mb-2">
                  <span className="text-sm font-medium text-gray-600">View to Cart Rate</span>
                  <span className="text-sm font-semibold text-gray-900">
                    {productFunnelData[0]?.value > 0
                      ? formatPercentage((productFunnelData[1]?.value / productFunnelData[0]?.value) * 100)
                      : '0%'}
                  </span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2">
                  <div
                    className="bg-green-600 h-2 rounded-full"
                    style={{
                      width: productFunnelData[0]?.value > 0
                        ? `${(productFunnelData[1]?.value / productFunnelData[0]?.value) * 100}%`
                        : '0%'
                    }}
                  ></div>
                </div>
              </div>

              <div className="p-4 bg-gray-50 rounded-lg">
                <div className="flex items-center justify-between mb-2">
                  <span className="text-sm font-medium text-gray-600">Cart to Purchase Rate</span>
                  <span className="text-sm font-semibold text-gray-900">
                    {productFunnelData[1]?.value > 0
                      ? formatPercentage((productFunnelData[2]?.value / productFunnelData[1]?.value) * 100)
                      : '0%'}
                  </span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2">
                  <div
                    className="bg-blue-600 h-2 rounded-full"
                    style={{
                      width: productFunnelData[1]?.value > 0
                        ? `${(productFunnelData[2]?.value / productFunnelData[1]?.value) * 100}%`
                        : '0%'
                    }}
                  ></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Traffic Overview */}
      {analytics.length > 0 && (
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Traffic Overview</h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="text-center">
              <EyeIcon className="h-12 w-12 text-gray-400 mx-auto mb-2" />
              <p className="text-2xl font-bold text-gray-900">
                {analytics.reduce((sum, day) => sum + day.totalVisits, 0).toLocaleString()}
              </p>
              <p className="text-sm text-gray-600">Total Visits</p>
            </div>
            <div className="text-center">
              <UserGroupIcon className="h-12 w-12 text-gray-400 mx-auto mb-2" />
              <p className="text-2xl font-bold text-gray-900">
                {analytics.reduce((sum, day) => sum + day.uniqueVisitors, 0).toLocaleString()}
              </p>
              <p className="text-sm text-gray-600">Unique Visitors</p>
            </div>
            <div className="text-center">
              <ChartBarIcon className="h-12 w-12 text-gray-400 mx-auto mb-2" />
              <p className="text-2xl font-bold text-gray-900">
                {analytics.length > 0
                  ? formatPercentage(
                      analytics.reduce((sum, day) => sum + day.bounceRate, 0) / analytics.length
                    )
                  : '0%'}
              </p>
              <p className="text-sm text-gray-600">Avg Bounce Rate</p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default StoreAnalytics;