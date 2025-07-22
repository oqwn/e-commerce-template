import React from 'react';
import { Link } from 'react-router-dom';
import { StarIcon as StarIconOutline, HeartIcon } from '@heroicons/react/24/outline';
import { StarIcon as StarIconSolid, HeartIcon as HeartIconSolid } from '@heroicons/react/24/solid';
import { Product } from '@/types';
import AnimatedAddToCartButton from '@/components/ui/AnimatedAddToCartButton';
import { useWishlist } from '@/hooks/useWishlist';
import { FlashSaleBadge } from '@/components/flashSales/FlashSaleBadge';
import { CountdownTimer } from '@/components/flashSales/CountdownTimer';
import { useFlashSaleByProduct } from '@/hooks/useFlashSales';

interface ProductCardProps {
  product: Product;
  className?: string;
}

export const ProductCard: React.FC<ProductCardProps> = ({ product, className = '' }) => {
  const { isInWishlist, toggleWishlist, isTogglingWishlist } = useWishlist();
  const { data: flashSale } = useFlashSaleByProduct(product.id);

  const renderStars = (rating: number) => {
    const stars = [];
    const fullStars = Math.floor(rating);

    for (let i = 0; i < 5; i++) {
      if (i < fullStars) {
        stars.push(<StarIconSolid key={i} className="h-4 w-4 text-yellow-400" />);
      } else {
        stars.push(<StarIconOutline key={i} className="h-4 w-4 text-gray-300" />);
      }
    }

    return stars;
  };

  const hasFlashSale = flashSale && !flashSale.expired;
  const displayPrice = hasFlashSale ? flashSale.salePrice : product.price;
  const originalPrice = hasFlashSale ? flashSale.originalPrice : product.compareAtPrice;

  return (
    <div className={`bg-white rounded-lg shadow-sm border hover:shadow-md transition-shadow group ${className}`}>
      <Link to={`/buyer/products/${product.slug}`}>
        <div className="aspect-square bg-gray-100 rounded-t-lg overflow-hidden relative">
          {product.images && product.images.length > 0 ? (
            <img
              src={product.images[0].imageUrl}
              alt={product.name}
              className="w-full h-full object-cover group-hover:scale-105 transition-transform"
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center">
              <div className="text-gray-400">No Image</div>
            </div>
          )}
          
          {/* Flash Sale Badge */}
          {hasFlashSale && (
            <div className="absolute top-2 left-2">
              <FlashSaleBadge
                discountPercentage={Math.round(flashSale.discountPercentage)}
                size="sm"
              />
            </div>
          )}

          {/* Wishlist Button */}
          <button 
            onClick={(e) => {
              e.preventDefault();
              toggleWishlist(product.id);
            }}
            disabled={isTogglingWishlist}
            className="absolute top-2 right-2 p-2 bg-white rounded-full shadow-sm hover:bg-gray-50 transition-colors disabled:opacity-50"
          >
            {isInWishlist(product.id) ? (
              <HeartIconSolid className="h-5 w-5 text-red-500" />
            ) : (
              <HeartIcon className="h-5 w-5 text-gray-600" />
            )}
          </button>

          {/* Discount Badge (non-flash sale) */}
          {!hasFlashSale && product.discountPercentage && (
            <div className="absolute top-2 left-2">
              <span className="bg-red-500 text-white text-xs px-2 py-1 rounded">
                -{product.discountPercentage}%
              </span>
            </div>
          )}
        </div>
      </Link>

      <div className="p-4">
        <Link to={`/buyer/products/${product.slug}`}>
          <h3 className="font-medium text-gray-900 mb-2 line-clamp-2 hover:text-blue-600">
            {product.name}
          </h3>
        </Link>
        
        {/* Flash Sale Countdown */}
        {hasFlashSale && flashSale.flashSaleEndTime && (
          <div className="mb-2">
            <CountdownTimer
              endTime={flashSale.flashSaleEndTime}
              size="sm"
              className="justify-start"
            />
          </div>
        )}
        
        {/* Rating */}
        {product.averageRating && (
          <div className="flex items-center mb-2">
            <div className="flex items-center">
              {renderStars(product.averageRating)}
            </div>
            <span className="text-sm text-gray-600 ml-2">
              ({product.reviewCount || 0})
            </span>
          </div>
        )}

        {/* Price */}
        <div className="flex items-center justify-between mb-3">
          <div className="flex items-center space-x-2">
            <span className={`text-lg font-bold ${hasFlashSale ? 'text-red-600' : 'text-gray-900'}`}>
              ${displayPrice}
            </span>
            {originalPrice && (
              <span className="text-sm text-gray-500 line-through">
                ${originalPrice}
              </span>
            )}
          </div>
        </div>

        {/* Add to Cart Button */}
        <AnimatedAddToCartButton
          productId={product.id}
          productName={product.name}
          productImageUrl={product.images?.[0]?.imageUrl}
          className="w-full"
          variant="primary"
          size="md"
        />
      </div>
    </div>
  );
};