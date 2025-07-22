import React from 'react';
import { Link } from 'react-router-dom';
import { ChevronRightIcon, TagIcon } from '@heroicons/react/24/outline';
import { Category } from '@/types';

interface SubcategoryListProps {
  categories: Category[];
  className?: string;
  layout?: 'horizontal' | 'vertical' | 'grid';
  showProductCount?: boolean;
}

export const SubcategoryList: React.FC<SubcategoryListProps> = ({ 
  categories, 
  className = '',
  layout = 'horizontal',
  showProductCount = true
}) => {
  if (!categories || categories.length === 0) {
    return null;
  }

  const getLayoutClasses = () => {
    switch (layout) {
      case 'vertical':
        return 'flex flex-col space-y-2';
      case 'grid':
        return 'grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3';
      default:
        return 'flex flex-wrap gap-2';
    }
  };

  const getItemClasses = () => {
    switch (layout) {
      case 'vertical':
        return 'flex items-center justify-between p-3 bg-white border rounded-lg hover:bg-gray-50 transition-colors';
      case 'grid':
        return 'flex items-center p-3 bg-white border rounded-lg hover:bg-gray-50 transition-colors';
      default:
        return 'inline-flex items-center px-3 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors';
    }
  };

  if (layout === 'vertical') {
    return (
      <div className={`${getLayoutClasses()} ${className}`}>
        {categories.map((category) => (
          <Link
            key={category.id}
            to={`/buyer/categories/${category.slug}`}
            className={getItemClasses()}
          >
            <div className="flex items-center flex-1">
              {category.imageUrl ? (
                <img
                  src={category.imageUrl}
                  alt={category.name}
                  className="h-8 w-8 rounded object-cover mr-3"
                />
              ) : (
                <TagIcon className="h-6 w-6 text-gray-400 mr-3" />
              )}
              <div>
                <div className="font-medium text-gray-900">{category.name}</div>
                {category.description && (
                  <div className="text-sm text-gray-600 line-clamp-1">
                    {category.description}
                  </div>
                )}
              </div>
            </div>
            <div className="flex items-center space-x-2">
              {showProductCount && category.productCount && (
                <span className="text-sm text-gray-500">
                  {category.productCount} items
                </span>
              )}
              <ChevronRightIcon className="h-4 w-4 text-gray-400" />
            </div>
          </Link>
        ))}
      </div>
    );
  }

  if (layout === 'grid') {
    return (
      <div className={`${getLayoutClasses()} ${className}`}>
        {categories.map((category) => (
          <Link
            key={category.id}
            to={`/buyer/categories/${category.slug}`}
            className={getItemClasses()}
          >
            {category.imageUrl ? (
              <img
                src={category.imageUrl}
                alt={category.name}
                className="h-6 w-6 rounded object-cover mr-2"
              />
            ) : (
              <TagIcon className="h-5 w-5 text-gray-400 mr-2" />
            )}
            <span className="font-medium text-gray-900 text-sm line-clamp-1">
              {category.name}
            </span>
            {showProductCount && category.productCount && (
              <span className="text-xs text-gray-500 ml-auto">
                {category.productCount}
              </span>
            )}
          </Link>
        ))}
      </div>
    );
  }

  // Default horizontal layout
  return (
    <div className={`${getLayoutClasses()} ${className}`}>
      {categories.map((category) => (
        <Link
          key={category.id}
          to={`/buyer/categories/${category.slug}`}
          className={getItemClasses()}
        >
          <span className="font-medium">{category.name}</span>
          {showProductCount && category.productCount && (
            <span className="ml-2 text-sm opacity-75">
              ({category.productCount})
            </span>
          )}
          <ChevronRightIcon className="ml-1 h-4 w-4" />
        </Link>
      ))}
    </div>
  );
};