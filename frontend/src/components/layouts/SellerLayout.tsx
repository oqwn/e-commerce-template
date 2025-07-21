import React, { useState } from 'react';
import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '@/contexts/useAuth';
import {
  BuildingStorefrontIcon,
  ChartBarIcon,
  ShoppingBagIcon,
  CurrencyDollarIcon,
  UserGroupIcon,
  Cog6ToothIcon,
  BellIcon,
  Bars3Icon,
  XMarkIcon,
  UserCircleIcon,
} from '@heroicons/react/24/outline';

const SellerLayout: React.FC = () => {
  const { user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  const navigation = [
    { name: 'Dashboard', href: '/seller', icon: ChartBarIcon },
    { name: 'Products', href: '/seller/products', icon: ShoppingBagIcon },
    { name: 'Orders', href: '/seller/orders', icon: UserGroupIcon },
    { name: 'Analytics', href: '/seller/analytics', icon: ChartBarIcon },
    { name: 'Finance', href: '/seller/finance', icon: CurrencyDollarIcon },
    { name: 'Store Settings', href: '/seller/store', icon: BuildingStorefrontIcon },
    { name: 'Account', href: '/seller/account', icon: Cog6ToothIcon },
  ];

  const getCurrentPageTitle = () => {
    const currentPath = location.pathname;
    const currentNav = navigation.find(item => item.href === currentPath);
    if (currentNav) return currentNav.name;
    
    // Handle sub-routes
    if (currentPath.startsWith('/seller/store')) return 'Store Settings';
    if (currentPath.startsWith('/seller/products')) return 'Products';
    if (currentPath.startsWith('/seller/orders')) return 'Orders';
    
    return 'Seller Dashboard';
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-gray-100 flex">
      {/* Mobile sidebar backdrop */}
      {isSidebarOpen && (
        <div
          className="fixed inset-0 z-40 lg:hidden"
          onClick={() => setIsSidebarOpen(false)}
        >
          <div className="fixed inset-0 bg-gray-600 bg-opacity-75" />
        </div>
      )}

      {/* Sidebar */}
      <div
        className={`fixed inset-y-0 left-0 z-50 w-64 bg-white shadow-lg transform ${
          isSidebarOpen ? 'translate-x-0' : '-translate-x-full'
        } transition-transform duration-300 ease-in-out lg:translate-x-0 lg:static lg:flex lg:flex-col`}
      >
        <div className="flex items-center justify-between h-16 px-6 border-b border-gray-200">
          <div className="flex items-center">
            <BuildingStorefrontIcon className="h-8 w-8 text-green-600" />
            <span className="ml-2 text-xl font-bold text-gray-900">Seller Hub</span>
          </div>
          <button
            onClick={() => setIsSidebarOpen(false)}
            className="lg:hidden p-2 rounded-md text-gray-400 hover:text-gray-500"
          >
            <XMarkIcon className="h-6 w-6" />
          </button>
        </div>

        <nav className="mt-6 px-3 flex-1">
          <div className="space-y-1">
            {navigation.map((item) => {
              const isActive = location.pathname === item.href;
              return (
                <Link
                  key={item.name}
                  to={item.href}
                  className={`group flex items-center px-3 py-2 text-sm font-medium rounded-md transition-colors ${
                    isActive
                      ? 'bg-green-100 text-green-900'
                      : 'text-gray-700 hover:bg-gray-100 hover:text-gray-900'
                  }`}
                  onClick={() => setIsSidebarOpen(false)}
                >
                  <item.icon
                    className={`mr-3 h-5 w-5 ${
                      isActive ? 'text-green-500' : 'text-gray-400 group-hover:text-gray-500'
                    }`}
                  />
                  {item.name}
                </Link>
              );
            })}
          </div>
        </nav>
      </div>

      {/* Main content */}
      <div className="flex-1 flex flex-col lg:ml-0">
        {/* Top header */}
        <header className="bg-white shadow-sm border-b border-gray-200">
          <div className="px-4 sm:px-6 lg:px-8">
            <div className="flex justify-between items-center h-16">
              <div className="flex items-center">
                <button
                  onClick={() => setIsSidebarOpen(true)}
                  className="lg:hidden p-2 rounded-md text-gray-400 hover:text-gray-500"
                >
                  <Bars3Icon className="h-6 w-6" />
                </button>
                <h1 className="text-xl font-semibold text-gray-900 ml-2 lg:ml-0">
                  {getCurrentPageTitle()}
                </h1>
              </div>

              <div className="flex items-center space-x-4">
                {/* Notifications */}
                <button className="p-2 text-gray-400 hover:text-gray-500 relative">
                  <BellIcon className="h-6 w-6" />
                  <span className="absolute -top-1 -right-1 h-4 w-4 bg-red-500 text-white text-xs rounded-full flex items-center justify-center">
                    3
                  </span>
                </button>

                {/* User menu */}
                <div className="relative group">
                  <button className="flex items-center space-x-2 text-gray-700 hover:text-gray-900">
                    <UserCircleIcon className="h-8 w-8" />
                    <div className="text-left">
                      <p className="text-sm font-medium">{user?.firstName} {user?.lastName}</p>
                      <p className="text-xs text-gray-500">Seller</p>
                    </div>
                  </button>
                  
                  {/* Dropdown */}
                  <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-50 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200">
                    <Link
                      to="/seller/profile"
                      className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                    >
                      My Profile
                    </Link>
                    <Link
                      to="/seller/store"
                      className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                    >
                      Store Settings
                    </Link>
                    <Link
                      to="/seller/help"
                      className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                    >
                      Help & Support
                    </Link>
                    <button
                      onClick={handleLogout}
                      className="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                    >
                      Sign Out
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </header>

        {/* Page content */}
        <main className="flex-1 p-4 sm:p-6 lg:p-8 bg-gray-100">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default SellerLayout;