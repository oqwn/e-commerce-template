import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Clock, Flame, ShoppingCart } from 'lucide-react';
import { useActiveFlashSaleProducts } from '@/hooks/useFlashSales';
import { CountdownTimer } from '@/components/flashSales/CountdownTimer';
import { FlashSaleBadge } from '@/components/flashSales/FlashSaleBadge';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import AnimatedAddToCartButton from '@/components/ui/AnimatedAddToCartButton';

export default function FlashSales() {
  const [currentPage, setCurrentPage] = useState(0);
  const { data, isLoading, error } = useActiveFlashSaleProducts();

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <LoadingSpinner />
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-12">
        <p className="text-red-600">Failed to load flash sales</p>
      </div>
    );
  }

  const products = data?.content || [];
  const totalPages = data?.totalPages || 0;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="mb-8 text-center">
        <div className="flex items-center justify-center mb-4">
          <Flame className="w-8 h-8 text-red-500 mr-2" />
          <h1 className="text-3xl font-bold text-gray-900">Flash Sales</h1>
        </div>
        <p className="text-gray-600 max-w-2xl mx-auto">
          Don't miss out on these limited-time offers! Grab amazing products at unbeatable prices 
          before they're gone.
        </p>
      </div>

      {/* Flash Sale Banner */}
      <div className="bg-gradient-to-r from-red-500 to-pink-600 text-white rounded-xl p-6 mb-8">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-xl font-bold mb-2">⚡ Limited Time Offers!</h2>
            <p className="opacity-90">Hurry up! These deals won't last long.</p>
          </div>
          <div className="text-right">
            <div className="text-sm opacity-90 mb-1">Deals ending soon</div>
            <Clock className="w-8 h-8 mx-auto animate-pulse" />
          </div>
        </div>
      </div>

      {products.length === 0 ? (
        <div className="text-center py-16 bg-white rounded-lg shadow-sm border">
          <Flame className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <h3 className="text-xl font-semibold text-gray-900 mb-2">
            No Flash Sales Active
          </h3>
          <p className="text-gray-600 mb-6">
            Check back later for amazing deals and limited-time offers
          </p>
          <Link
            to="/buyer/products"
            className="inline-flex items-center px-6 py-3 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
          >
            <ShoppingCart className="w-5 h-5 mr-2" />
            Browse All Products
          </Link>
        </div>
      ) : (
        <>
          {/* Flash Sale Products Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {products.map((product: any) => (
              <div
                key={product.id}
                className="bg-white rounded-lg shadow-sm border hover:shadow-lg transition-all duration-300 overflow-hidden"
              >
                {/* Product Image */}
                <Link to={`/buyer/products/${product.productSlug}`}>
                  <div className="aspect-square bg-gray-100 overflow-hidden relative group">
                    {product.productImage ? (
                      <img
                        src={product.productImage}
                        alt={product.productName}
                        className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
                      />
                    ) : (
                      <div className="w-full h-full flex items-center justify-center">
                        <div className="text-gray-400">No Image</div>
                      </div>
                    )}
                    
                    {/* Flash Sale Badge */}
                    <div className="absolute top-3 left-3">
                      <FlashSaleBadge
                        discountPercentage={Math.round(product.discountPercentage)}
                        size="sm"
                      />
                    </div>

                    {/* Urgency Indicator */}
                    <div className="absolute top-3 right-3 bg-white bg-opacity-90 px-2 py-1 rounded-full">
                      <span className="text-xs font-semibold text-red-600">
                        {product.remainingQuantity} left
                      </span>
                    </div>
                  </div>
                </Link>

                {/* Product Details */}
                <div className="p-4">
                  <Link to={`/buyer/products/${product.productSlug}`}>
                    <h3 className="font-semibold text-gray-900 mb-2 line-clamp-2 hover:text-blue-600 transition-colors">
                      {product.productName}
                    </h3>
                  </Link>
                  
                  <p className="text-sm text-gray-600 mb-3">{product.sellerName}</p>

                  {/* Countdown Timer */}
                  {product.flashSaleEndTime && (
                    <div className="mb-3">
                      <CountdownTimer
                        endTime={product.flashSaleEndTime}
                        size="sm"
                        className="justify-start"
                      />
                    </div>
                  )}

                  {/* Pricing */}
                  <div className="mb-4">
                    <div className="flex items-center space-x-2 mb-1">
                      <span className="text-xl font-bold text-red-600">
                        ${product.salePrice}
                      </span>
                      <span className="text-sm text-gray-500 line-through">
                        ${product.originalPrice}
                      </span>
                    </div>
                    <div className="text-sm text-green-600 font-medium">
                      Save ${(product.originalPrice - product.salePrice).toFixed(2)}
                    </div>
                  </div>

                  {/* Add to Cart */}
                  <AnimatedAddToCartButton
                    productId={product.productId}
                    productName={product.productName}
                    productImageUrl={product.productImage}
                    className="w-full"
                    size="md"
                    disabled={!product.hasQuantityAvailable}
                  />

                  {/* Stock Warning */}
                  {product.remainingQuantity <= 5 && product.remainingQuantity > 0 && (
                    <p className="text-xs text-red-600 mt-2 text-center font-medium">
                      Only {product.remainingQuantity} left in stock!
                    </p>
                  )}
                </div>
              </div>
            ))}
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="mt-8 flex justify-center">
              <nav className="flex space-x-2">
                <button
                  onClick={() => setCurrentPage(prev => Math.max(0, prev - 1))}
                  disabled={currentPage === 0}
                  className="px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Previous
                </button>
                {[...Array(totalPages)].map((_, index) => (
                  <button
                    key={index}
                    onClick={() => setCurrentPage(index)}
                    className={`px-4 py-2 border rounded-md ${
                      currentPage === index
                        ? 'bg-red-600 text-white border-red-600'
                        : 'border-gray-300 hover:bg-gray-50'
                    }`}
                  >
                    {index + 1}
                  </button>
                ))}
                <button
                  onClick={() => setCurrentPage(prev => Math.min(totalPages - 1, prev + 1))}
                  disabled={currentPage === totalPages - 1}
                  className="px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Next
                </button>
              </nav>
            </div>
          )}
        </>
      )}
    </div>
  );
}