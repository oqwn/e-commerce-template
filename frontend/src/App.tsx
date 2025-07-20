import { Routes, Route, Navigate } from 'react-router-dom'
import { Suspense, lazy } from 'react'

import Layout from '@/components/Layout'
import LoadingSpinner from '@/components/common/LoadingSpinner'

// Lazy load pages for code splitting
const Home = lazy(() => import('@/pages/Home'))
const Products = lazy(() => import('@/pages/Products'))
const Settings = lazy(() => import('@/pages/Settings'))

function App() {
  return (
    <Layout>
      <Suspense fallback={<LoadingSpinner />}>
        <Routes>
          <Route path="/" element={<Navigate to="/home" replace />} />
          <Route path="/home" element={<Home />} />
          <Route path="/products" element={<Products />} />
          <Route path="/settings" element={<Settings />} />
          <Route path="*" element={<Navigate to="/home" replace />} />
        </Routes>
      </Suspense>
    </Layout>
  )
}

export default App