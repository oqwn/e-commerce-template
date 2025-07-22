import React from 'react';
import { Link } from 'react-router-dom';
import { ChevronRightIcon, TagIcon } from '@heroicons/react/24/outline';
import { Category } from '@/types';

interface CategoryCardProps {
  category: Category;
  className?: string;
  showChildren?: boolean;
}

export const CategoryCard: React.FC<CategoryCardProps> = ({ 
  category, 
  className = '',
  showChildren = true
}) => {
  const hasChildren = category.children && category.children.length > 0;
  const productCount = category.productCount || 0;

  return (
    <div className={`bg-white rounded-lg shadow-sm border hover:shadow-md transition-all duration-200 group ${className}`}>
      <Link to={`/buyer/categories/${category.slug}`}>
        <div className="aspect-square bg-gray-50 rounded-t-lg overflow-hidden relative">
          {category.imageUrl ? (
            <img
              src={category.imageUrl}
              alt={category.name}
              className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-200"
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center">
              <TagIcon className="h-16 w-16 text-gray-400" />
            </div>
          )}
          
          {/* Children Indicator */}
          {hasChildren && (
            <div className="absolute top-2 right-2 bg-white bg-opacity-90 rounded-full p-1">
              <ChevronRightIcon className="h-4 w-4 text-gray-600" />
            </div>
          )}

          {/* Product Count Badge */}
          {productCount > 0 && (
            <div className="absolute bottom-2 left-2">
              <span className="bg-blue-600 text-white text-xs px-2 py-1 rounded-full">
                {productCount} items
              </span>
            </div>
          )}
        </div>
      </Link>

      <div className="p-4">
        <Link to={`/buyer/categories/${category.slug}`}>
          <h3 className="font-semibold text-gray-900 mb-1 line-clamp-2 hover:text-blue-600 transition-colors">
            {category.name}
          </h3>
        </Link>
        
        {/* Description */}
        {category.description && (
          <p className="text-sm text-gray-600 mb-3 line-clamp-2">
            {category.description}
          </p>
        )}

        {/* Children Categories */}
        {showChildren && hasChildren && (
          <div className="mt-3 border-t pt-3">
            <p className="text-xs text-gray-500 mb-2">Subcategories:</p>
            <div className="flex flex-wrap gap-1">
              {category.children!.slice(0, 3).map((child) => (
                <Link
                  key={child.id}
                  to={`/buyer/categories/${child.slug}`}
                  className="text-xs bg-gray-100 text-gray-700 px-2 py-1 rounded hover:bg-gray-200 transition-colors"
                  onClick={(e) => e.stopPropagation()}
                >
                  {child.name}
                </Link>
              ))}
              {category.children!.length > 3 && (
                <span className="text-xs text-gray-500 px-2 py-1">
                  +{category.children!.length - 3} more
                </span>
              )}
            </div>
          </div>
        )}

        {/* Product Count (if no children to show) */}
        {(!showChildren || !hasChildren) && productCount > 0 && (
          <div className="mt-2 text-sm text-gray-600">
            {productCount} {productCount === 1 ? 'product' : 'products'}
          </div>
        )}

        {/* View Category Button */}
        <Link
          to={`/buyer/categories/${category.slug}`}
          className="mt-3 w-full inline-flex items-center justify-center px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 transition-colors"
        >
          View Category
          <ChevronRightIcon className="ml-1 h-4 w-4" />
        </Link>
      </div>
    </div>
  );
};