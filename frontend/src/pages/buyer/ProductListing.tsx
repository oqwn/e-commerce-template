import React, { useState, useEffect } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import {
  MagnifyingGlassIcon,
  HeartIcon,
  StarIcon,
  ShoppingCartIcon,
} from '@heroicons/react/24/outline';
import { StarIcon as StarIconSolid } from '@heroicons/react/24/solid';
import { Product, Category } from '@/types';
import { api } from '@/services/api';

const ProductListing: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState(searchParams.get('q') || '');
  const [selectedCategory, setSelectedCategory] = useState(searchParams.get('category') || '');
  const [sortBy, setSortBy] = useState(searchParams.get('sort') || 'name');
  const [priceRange, setPriceRange] = useState({
    min: searchParams.get('min_price') || '',
    max: searchParams.get('max_price') || ''
  });

  useEffect(() => {
    fetchProducts();
    fetchCategories();
  }, [searchParams]);

  const fetchProducts = async () => {
    try {
      setIsLoading(true);
      const params = new URLSearchParams();
      
      if (searchQuery) params.append('keyword', searchQuery);
      if (selectedCategory) params.append('categoryId', selectedCategory);
      if (priceRange.min) params.append('minPrice', priceRange.min);
      if (priceRange.max) params.append('maxPrice', priceRange.max);
      params.append('sortBy', sortBy);
      params.append('limit', '20');

      const response = await api.post('/products/search', {
        keyword: searchQuery || undefined,
        categoryId: selectedCategory ? parseInt(selectedCategory) : undefined,
        minPrice: priceRange.min ? parseFloat(priceRange.min) : undefined,
        maxPrice: priceRange.max ? parseFloat(priceRange.max) : undefined,
        sortBy,
        limit: 20,
        offset: 0
      });

      if (response.success) {
        setProducts(response.data);
      }
    } catch (error) {
      console.error('Error fetching products:', error);
      // Fallback to featured products if search fails
      try {
        const response = await api.get('/products/featured?limit=20');
        if (response.success) {
          setProducts(response.data);
        }
      } catch (fallbackError) {
        console.error('Error fetching fallback products:', fallbackError);
      }
    } finally {
      setIsLoading(false);
    }
  };

  const fetchCategories = async () => {
    try {
      const response = await api.get('/categories');
      if (response.success) {
        setCategories(response.data);
      }
    } catch (error) {
      console.error('Error fetching categories:', error);
    }
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    updateSearchParams();
  };

  const updateSearchParams = () => {
    const params = new URLSearchParams();
    if (searchQuery) params.append('q', searchQuery);
    if (selectedCategory) params.append('category', selectedCategory);
    if (priceRange.min) params.append('min_price', priceRange.min);
    if (priceRange.max) params.append('max_price', priceRange.max);
    if (sortBy !== 'name') params.append('sort', sortBy);
    
    setSearchParams(params);
  };

  const renderStars = (rating: number) => {
    const stars = [];
    const fullStars = Math.floor(rating);

    for (let i = 0; i < 5; i++) {
      if (i < fullStars) {
        stars.push(<StarIconSolid key={i} className="h-4 w-4 text-yellow-400" />);
      } else {
        stars.push(<StarIcon key={i} className="h-4 w-4 text-gray-300" />);
      }
    }

    return stars;
  };

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div className="animate-pulse">
          <div className="h-12 bg-gray-200 rounded-lg mb-6"></div>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {[...Array(8)].map((_, i) => (
              <div key={i} className="h-80 bg-gray-200 rounded-lg"></div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Search and Filter Header */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <form onSubmit={handleSearch} className="space-y-4">
          <div className="flex flex-col lg:flex-row lg:items-center lg:space-x-4 space-y-4 lg:space-y-0">
            {/* Search Input */}
            <div className="flex-1">
              <div className="relative">
                <MagnifyingGlassIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder="Search products..."
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
            </div>

            {/* Category Filter */}
            <div className="lg:w-48">
              <select
                value={selectedCategory}
                onChange={(e) => setSelectedCategory(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="">All Categories</option>
                {categories.map((category) => (
                  <option key={category.id} value={category.id}>
                    {category.name}
                  </option>
                ))}
              </select>
            </div>

            {/* Sort */}
            <div className="lg:w-48">
              <select
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="name">Sort by Name</option>
                <option value="price">Sort by Price</option>
                <option value="created_at">Sort by Newest</option>
                <option value="rating">Sort by Rating</option>
              </select>
            </div>

            <button
              type="submit"
              className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              Search
            </button>
          </div>

          {/* Price Range */}
          <div className="flex items-center space-x-4">
            <span className="text-sm text-gray-600">Price Range:</span>
            <input
              type="number"
              value={priceRange.min}
              onChange={(e) => setPriceRange(prev => ({ ...prev, min: e.target.value }))}
              placeholder="Min"
              className="w-24 px-3 py-1 text-sm border border-gray-300 rounded"
            />
            <span className="text-gray-400">-</span>
            <input
              type="number"
              value={priceRange.max}
              onChange={(e) => setPriceRange(prev => ({ ...prev, max: e.target.value }))}
              placeholder="Max"
              className="w-24 px-3 py-1 text-sm border border-gray-300 rounded"
            />
          </div>
        </form>
      </div>

      {/* Results Header */}
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">
          {searchQuery ? `Search results for "${searchQuery}"` : 'All Products'}
        </h1>
        <p className="text-gray-600">{products.length} products found</p>
      </div>

      {/* Product Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {products.map((product) => (
          <div key={product.id} className="bg-white rounded-lg shadow-sm border hover:shadow-md transition-shadow group">
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
                
                {/* Wishlist Button */}
                <button className="absolute top-2 right-2 p-2 bg-white rounded-full shadow-sm hover:bg-gray-50 transition-colors">
                  <HeartIcon className="h-5 w-5 text-gray-600" />
                </button>

                {/* Discount Badge */}
                {product.discountPercentage && (
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
                  <span className="text-lg font-bold text-gray-900">
                    ${product.price}
                  </span>
                  {product.compareAtPrice && (
                    <span className="text-sm text-gray-500 line-through">
                      ${product.compareAtPrice}
                    </span>
                  )}
                </div>
              </div>

              {/* Add to Cart Button */}
              <button className="w-full flex items-center justify-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
                <ShoppingCartIcon className="h-5 w-5 mr-2" />
                Add to Cart
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Empty State */}
      {products.length === 0 && !isLoading && (
        <div className="text-center py-12">
          <div className="text-gray-400 mb-4">
            <MagnifyingGlassIcon className="h-16 w-16 mx-auto" />
          </div>
          <h3 className="text-lg font-medium text-gray-900 mb-2">No products found</h3>
          <p className="text-gray-600 mb-4">Try adjusting your search criteria</p>
          <button
            onClick={() => {
              setSearchQuery('');
              setSelectedCategory('');
              setPriceRange({ min: '', max: '' });
              setSearchParams({});
            }}
            className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            Clear Filters
          </button>
        </div>
      )}
    </div>
  );
};

export default ProductListing;