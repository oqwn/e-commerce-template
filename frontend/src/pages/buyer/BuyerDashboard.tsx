import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  ShoppingBagIcon,
  SparklesIcon,
  FireIcon,
  TagIcon,
  ArrowRightIcon,
} from '@heroicons/react/24/outline';
import { Product, Category } from '@/types';
import { api } from '@/services/api';

const BuyerDashboard: React.FC = () => {
  const [featuredProducts, setFeaturedProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [productsResponse, categoriesResponse] = await Promise.all([
          api.get('/products/featured?limit=8'),
          api.get('/categories'),
        ]);

        if (productsResponse.success) {
          setFeaturedProducts(productsResponse.data);
        }
        if (categoriesResponse.success) {
          setCategories(categoriesResponse.data.slice(0, 6));
        }
      } catch (error) {
        console.error('Error fetching data:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, []);

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div className="animate-pulse">
          <div className="h-64 bg-gray-200 rounded-lg mb-6"></div>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {[...Array(8)].map((_, i) => (
              <div key={i} className="h-64 bg-gray-200 rounded-lg"></div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      {/* Hero Section */}
      <div className="bg-gradient-to-r from-blue-600 to-purple-600 rounded-lg overflow-hidden">
        <div className="px-6 py-12 sm:px-12 sm:py-20">
          <div className="max-w-2xl">
            <h1 className="text-3xl sm:text-4xl font-bold text-white mb-4">
              Discover Amazing Products
            </h1>
            <p className="text-lg text-blue-100 mb-6">
              Shop from thousands of verified sellers and find everything you need in one place.
            </p>
            <Link
              to="/buyer/products"
              className="inline-flex items-center px-6 py-3 bg-white text-blue-600 font-semibold rounded-lg hover:bg-gray-100 transition-colors"
            >
              Start Shopping
              <ArrowRightIcon className="ml-2 h-5 w-5" />
            </Link>
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        <Link
          to="/buyer/categories"
          className="bg-white p-6 rounded-lg shadow-sm border hover:shadow-md transition-shadow group"
        >
          <div className="flex items-center">
            <div className="bg-blue-100 p-3 rounded-lg">
              <ShoppingBagIcon className="h-6 w-6 text-blue-600" />
            </div>
            <div className="ml-4">
              <h3 className="font-semibold text-gray-900 group-hover:text-blue-600">
                Browse Categories
              </h3>
              <p className="text-sm text-gray-500">Explore by category</p>
            </div>
          </div>
        </Link>

        <Link
          to="/buyer/deals"
          className="bg-white p-6 rounded-lg shadow-sm border hover:shadow-md transition-shadow group"
        >
          <div className="flex items-center">
            <div className="bg-red-100 p-3 rounded-lg">
              <TagIcon className="h-6 w-6 text-red-600" />
            </div>
            <div className="ml-4">
              <h3 className="font-semibold text-gray-900 group-hover:text-red-600">
                Today's Deals
              </h3>
              <p className="text-sm text-gray-500">Limited time offers</p>
            </div>
          </div>
        </Link>

        <Link
          to="/buyer/new-arrivals"
          className="bg-white p-6 rounded-lg shadow-sm border hover:shadow-md transition-shadow group"
        >
          <div className="flex items-center">
            <div className="bg-green-100 p-3 rounded-lg">
              <SparklesIcon className="h-6 w-6 text-green-600" />
            </div>
            <div className="ml-4">
              <h3 className="font-semibold text-gray-900 group-hover:text-green-600">
                New Arrivals
              </h3>
              <p className="text-sm text-gray-500">Latest products</p>
            </div>
          </div>
        </Link>

        <Link
          to="/buyer/trending"
          className="bg-white p-6 rounded-lg shadow-sm border hover:shadow-md transition-shadow group"
        >
          <div className="flex items-center">
            <div className="bg-orange-100 p-3 rounded-lg">
              <FireIcon className="h-6 w-6 text-orange-600" />
            </div>
            <div className="ml-4">
              <h3 className="font-semibold text-gray-900 group-hover:text-orange-600">
                Trending Now
              </h3>
              <p className="text-sm text-gray-500">Popular products</p>
            </div>
          </div>
        </Link>
      </div>

      {/* Featured Categories */}
      <div>
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-bold text-gray-900">Shop by Category</h2>
          <Link
            to="/buyer/categories"
            className="text-blue-600 hover:text-blue-700 font-medium"
          >
            View All →
          </Link>
        </div>
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-4">
          {categories.map((category) => (
            <Link
              key={category.id}
              to={`/buyer/categories/${category.slug}`}
              className="bg-white p-4 rounded-lg shadow-sm border hover:shadow-md transition-shadow text-center group"
            >
              <div className="w-16 h-16 bg-gray-100 rounded-full mx-auto mb-3 flex items-center justify-center">
                {category.imageUrl ? (
                  <img
                    src={category.imageUrl}
                    alt={category.name}
                    className="w-10 h-10 object-cover rounded-full"
                  />
                ) : (
                  <ShoppingBagIcon className="h-8 w-8 text-gray-400" />
                )}
              </div>
              <h3 className="font-medium text-gray-900 group-hover:text-blue-600">
                {category.name}
              </h3>
            </Link>
          ))}
        </div>
      </div>

      {/* Featured Products */}
      <div>
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-bold text-gray-900">Featured Products</h2>
          <Link
            to="/buyer/products"
            className="text-blue-600 hover:text-blue-700 font-medium"
          >
            View All →
          </Link>
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {featuredProducts.map((product) => (
            <Link
              key={product.id}
              to={`/buyer/products/${product.slug}`}
              className="bg-white rounded-lg shadow-sm border hover:shadow-md transition-shadow group"
            >
              <div className="aspect-square bg-gray-100 rounded-t-lg overflow-hidden">
                {product.images && product.images.length > 0 ? (
                  <img
                    src={product.images[0].imageUrl}
                    alt={product.name}
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform"
                  />
                ) : (
                  <div className="w-full h-full flex items-center justify-center">
                    <ShoppingBagIcon className="h-16 w-16 text-gray-400" />
                  </div>
                )}
              </div>
              <div className="p-4">
                <h3 className="font-medium text-gray-900 mb-2 line-clamp-2">
                  {product.name}
                </h3>
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <span className="text-lg font-bold text-gray-900">
                      ${product.price}
                    </span>
                    {product.compareAtPrice && (
                      <span className="text-sm text-gray-500 line-through">
                        ${product.compareAtPrice}
                      </span>
                    )}
                  </div>
                  {product.averageRating && (
                    <div className="flex items-center">
                      <span className="text-yellow-400">★</span>
                      <span className="text-sm text-gray-600 ml-1">
                        {product.averageRating}
                      </span>
                    </div>
                  )}
                </div>
                {product.discountPercentage && (
                  <div className="mt-2">
                    <span className="inline-block bg-red-100 text-red-800 text-xs px-2 py-1 rounded">
                      {product.discountPercentage}% OFF
                    </span>
                  </div>
                )}
              </div>
            </Link>
          ))}
        </div>
      </div>
    </div>
  );
};

export default BuyerDashboard;