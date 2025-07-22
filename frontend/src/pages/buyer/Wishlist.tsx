import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Trash2, ShoppingCart, Heart } from 'lucide-react';
import { useWishlistItems, useWishlist } from '@/hooks/useWishlist';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import AnimatedAddToCartButton from '@/components/ui/AnimatedAddToCartButton';
import { wishlistApi, WishlistItem } from '@/api/wishlist';
import { toast } from 'react-hot-toast';

export default function Wishlist() {
  const [currentPage, setCurrentPage] = useState(0);
  const { data, isLoading, error, refetch } = useWishlistItems(currentPage, 12);
  const { } = useWishlist(); // Removed unused toggleWishlist
  const [removingItems, setRemovingItems] = useState<Set<number>>(new Set());

  const handleRemoveFromWishlist = async (productId: number) => {
    setRemovingItems(prev => new Set(prev).add(productId));
    try {
      await wishlistApi.removeFromWishlist(productId);
      toast.success('Removed from wishlist');
      refetch();
    } catch (error) {
      toast.error('Failed to remove from wishlist');
    } finally {
      setRemovingItems(prev => {
        const newSet = new Set(prev);
        newSet.delete(productId);
        return newSet;
      });
    }
  };

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
        <p className="text-red-600">Failed to load wishlist</p>
        <button 
          onClick={() => refetch()}
          className="mt-4 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
        >
          Try Again
        </button>
      </div>
    );
  }

  const wishlistItems = data?.content || [];
  const totalPages = data?.totalPages || 0;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">My Wishlist</h1>
        <p className="mt-2 text-gray-600">
          {data?.totalElements || 0} items in your wishlist
        </p>
      </div>

      {wishlistItems.length === 0 ? (
        <div className="text-center py-16 bg-white rounded-lg shadow-sm border">
          <Heart className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <h2 className="text-xl font-semibold text-gray-900 mb-2">
            Your wishlist is empty
          </h2>
          <p className="text-gray-600 mb-6">
            Start adding products you love to keep track of them
          </p>
          <Link
            to="/buyer/products"
            className="inline-flex items-center px-6 py-3 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
          >
            <ShoppingCart className="w-5 h-5 mr-2" />
            Continue Shopping
          </Link>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {wishlistItems.map((item: WishlistItem) => (
              <div
                key={item.id}
                className={`bg-white rounded-lg shadow-sm border hover:shadow-md transition-shadow ${
                  removingItems.has(item.productId) ? 'opacity-50' : ''
                }`}
              >
                <Link to={`/buyer/products/${item.productId}`}>
                  <div className="aspect-square bg-gray-100 rounded-t-lg overflow-hidden">
                    {item.productImage ? (
                      <img
                        src={item.productImage}
                        alt={item.productName}
                        className="w-full h-full object-cover hover:scale-105 transition-transform"
                      />
                    ) : (
                      <div className="w-full h-full flex items-center justify-center">
                        <div className="text-gray-400">No Image</div>
                      </div>
                    )}
                  </div>
                </Link>

                <div className="p-4">
                  <Link to={`/buyer/products/${item.productId}`}>
                    <h3 className="font-medium text-gray-900 mb-1 line-clamp-2 hover:text-blue-600">
                      {item.productName}
                    </h3>
                  </Link>

                  <p className="text-sm text-gray-600 mb-2">{item.sellerName}</p>

                  <div className="flex items-center justify-between mb-3">
                    <span className="text-lg font-bold text-gray-900">
                      ${item.productPrice}
                    </span>
                  </div>

                  {item.notes && (
                    <p className="text-sm text-gray-600 mb-3 italic">
                      "{item.notes}"
                    </p>
                  )}

                  <div className="flex space-x-2">
                    <AnimatedAddToCartButton
                      productId={item.productId}
                      productName={item.productName}
                      productImageUrl={item.productImage ?? undefined}
                      className="flex-1"
                      size="sm"
                    />
                    <button
                      onClick={() => handleRemoveFromWishlist(item.productId)}
                      disabled={removingItems.has(item.productId)}
                      className="p-2 text-red-600 hover:bg-red-50 rounded-md transition-colors disabled:opacity-50"
                      title="Remove from wishlist"
                    >
                      <Trash2 className="w-5 h-5" />
                    </button>
                  </div>
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
                        ? 'bg-blue-600 text-white border-blue-600'
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