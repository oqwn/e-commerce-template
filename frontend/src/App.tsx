import { Routes, Route, Navigate } from 'react-router-dom'
import { Suspense, lazy } from 'react'
import { ToastContainer } from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'

import { AuthProvider } from '@/contexts/AuthContext'
import { CartProvider } from '@/contexts/CartContext'
import Layout from '@/components/Layout'
import LoadingSpinner from '@/components/common/LoadingSpinner'
import RoleBasedRouter from '@/components/RoleBasedRouter'
import RoleBasedRedirect from '@/components/RoleBasedRedirect'

// Layout components
import BuyerLayout from '@/components/layouts/BuyerLayout'
import SellerLayout from '@/components/layouts/SellerLayout'
import AdminLayout from '@/components/layouts/AdminLayout'

// Lazy load pages for code splitting
const Home = lazy(() => import('@/pages/Home'))
const Products = lazy(() => import('@/pages/Products'))
const Settings = lazy(() => import('@/pages/Settings'))
const Login = lazy(() => import('@/pages/Login'))
const Register = lazy(() => import('@/pages/Register'))
const Profile = lazy(() => import('@/pages/Profile'))
const ForgotPassword = lazy(() => import('@/pages/ForgotPassword'))
const ResetPassword = lazy(() => import('@/pages/ResetPassword'))
const VerifyEmailPrompt = lazy(() => import('@/pages/VerifyEmailPrompt'))
const Unauthorized = lazy(() => import('@/pages/Unauthorized'))

// Role-specific dashboards
const BuyerDashboard = lazy(() => import('@/pages/buyer/BuyerDashboard'))
const SellerDashboard = lazy(() => import('@/pages/seller/SellerDashboard'))
const AdminDashboard = lazy(() => import('@/pages/admin/AdminDashboard'))

// Buyer pages
const ProductListing = lazy(() => import('@/pages/buyer/ProductListing'))
const Cart = lazy(() => import('@/pages/Cart'))
const Checkout = lazy(() => import('@/pages/Checkout'))
const OrderConfirmation = lazy(() => import('@/pages/OrderConfirmation'))

// Seller pages
const StoreRegistration = lazy(() => import('@/pages/seller/StoreRegistration'))
const StoreManagement = lazy(() => import('@/pages/seller/StoreManagement'))
const StoreAnalytics = lazy(() => import('@/pages/seller/StoreAnalytics'))

function App() {
  return (
    <AuthProvider>
      <CartProvider>
        <Suspense fallback={<LoadingSpinner />}>
        <Routes>
          {/* Public routes without layout */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/reset-password" element={<ResetPassword />} />
          <Route path="/verify-email-prompt" element={<VerifyEmailPrompt />} />
          <Route path="/unauthorized" element={<Unauthorized />} />

          {/* Role-based redirect for authenticated users */}
          <Route path="/" element={<RoleBasedRedirect />} />

          {/* Global cart and checkout routes */}
          <Route path="/cart" element={<Cart />} />
          <Route path="/checkout" element={<Checkout />} />
          <Route path="/order-confirmation/:orderId" element={<OrderConfirmation />} />

          {/* Buyer Routes */}
          <Route path="/buyer/*" element={
            <RoleBasedRouter allowedRoles={['BUYER']}>
              <BuyerLayout />
            </RoleBasedRouter>
          }>
            <Route index element={<BuyerDashboard />} />
            <Route path="products" element={<ProductListing />} />
            <Route path="categories" element={<div>Categories Page</div>} />
            <Route path="wishlist" element={<div>Wishlist Page</div>} />
            <Route path="orders" element={<div>Orders Page</div>} />
            <Route path="cart" element={<Cart />} />
            <Route path="profile" element={<Profile />} />
            <Route path="settings" element={<Settings />} />
          </Route>

          {/* Seller Routes */}
          <Route path="/seller/*" element={
            <RoleBasedRouter allowedRoles={['SELLER']}>
              <SellerLayout />
            </RoleBasedRouter>
          }>
            <Route index element={<SellerDashboard />} />
            <Route path="store/register" element={<StoreRegistration />} />
            <Route path="store" element={<StoreManagement />} />
            <Route path="products" element={<div>Seller Products</div>} />
            <Route path="orders" element={<div>Seller Orders</div>} />
            <Route path="analytics" element={<StoreAnalytics />} />
            <Route path="finance" element={<div>Finance</div>} />
            <Route path="account" element={<div>Account Settings</div>} />
            <Route path="profile" element={<Profile />} />
          </Route>

          {/* Admin Routes */}
          <Route path="/admin/*" element={
            <RoleBasedRouter allowedRoles={['ADMIN']}>
              <AdminLayout />
            </RoleBasedRouter>
          }>
            <Route index element={<AdminDashboard />} />
            <Route path="users" element={<div>User Management</div>} />
            <Route path="sellers" element={<div>Seller Management</div>} />
            <Route path="products" element={<div>Product Moderation</div>} />
            <Route path="orders" element={<div>Order Oversight</div>} />
            <Route path="reports" element={<div>Reports</div>} />
            <Route path="moderation" element={<div>Content Moderation</div>} />
            <Route path="settings" element={<div>System Settings</div>} />
            <Route path="profile" element={<Profile />} />
          </Route>

          {/* Legacy routes with basic layout (for backward compatibility) */}
          <Route element={<Layout />}>
            <Route path="/home" element={<Home />} />
            <Route path="/products" element={<Products />} />
          </Route>

          {/* Catch all */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
        </Suspense>
        <ToastContainer position="top-right" autoClose={3000} />
      </CartProvider>
    </AuthProvider>
  )
}

export default App