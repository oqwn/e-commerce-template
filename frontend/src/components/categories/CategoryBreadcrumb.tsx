import React from 'react';
import { Link } from 'react-router-dom';
import { ChevronRightIcon, HomeIcon } from '@heroicons/react/24/outline';
import { Category } from '@/types';

interface CategoryBreadcrumbProps {
  category?: Category;
  customItems?: { label: string; href?: string }[];
  className?: string;
}

export const CategoryBreadcrumb: React.FC<CategoryBreadcrumbProps> = ({ 
  category, 
  customItems = [],
  className = '' 
}) => {
  // Build breadcrumb items
  const items = [
    { label: 'Home', href: '/buyer' },
    { label: 'Categories', href: '/buyer/categories' },
    ...customItems,
  ];

  // Add category and its parents to breadcrumb
  if (category) {
    const categoryPath = [];
    let current: Category | undefined = category;
    
    // Build path from current category up to root
    while (current) {
      categoryPath.unshift({
        label: current.name,
        href: `/buyer/categories/${current.slug}`,
        category: current
      });
      current = current.parent;
    }
    
    items.push(...categoryPath);
  }

  return (
    <nav className={`flex items-center space-x-1 text-sm text-gray-600 ${className}`} aria-label="Breadcrumb">
      <ol className="flex items-center space-x-1">
        {items.map((item, index) => (
          <li key={index} className="flex items-center">
            {index === 0 && (
              <HomeIcon className="h-4 w-4 mr-1 text-gray-400" />
            )}
            
            {item.href && index < items.length - 1 ? (
              <Link
                to={item.href}
                className="hover:text-blue-600 transition-colors"
              >
                {item.label}
              </Link>
            ) : (
              <span className={index === items.length - 1 ? 'text-gray-900 font-medium' : ''}>
                {item.label}
              </span>
            )}
            
            {index < items.length - 1 && (
              <ChevronRightIcon className="h-4 w-4 mx-2 text-gray-400" />
            )}
          </li>
        ))}
      </ol>
    </nav>
  );
};