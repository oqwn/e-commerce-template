import { useState, useMemo } from 'react';
import { useParams } from 'react-router-dom';
import { FunnelIcon } from '@heroicons/react/24/outline';
import { CategoryBreadcrumb } from '@/components/categories/CategoryBreadcrumb';
import { SubcategoryList } from '@/components/categories/SubcategoryList';
import { ProductCard } from '@/components/products/ProductCard';
import { useCategoryBySlug, useChildCategories, useProductsByCategory } from '@/hooks/useCategories';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import { Product } from '@/types';

export default function CategoryProducts() {
  const { slug } = useParams<{ slug: string }>();
  const [sortBy, setSortBy] = useState('featured');
  const [showFilters, setShowFilters] = useState(false);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize] = useState(12);

  // Fetch category data
  const { data: category, isLoading: categoryLoading, error: categoryError } = useCategoryBySlug(slug || null);
  const { data: subcategories } = useChildCategories(category?.id || null);
  
  // Fetch products for this category
  const { 
    data: productsData, 
    isLoading: productsLoading, 
    error: productsError 
  } = useProductsByCategory(slug || null, {
    page: currentPage,
    size: pageSize,
    sort: sortBy
  });

  const products = useMemo(() => {
    if (!productsData) return [];
    // Handle different response structures
    return productsData.content || productsData.data || productsData || [];
  }, [productsData]);

  const totalPages = useMemo(() => {
    if (!productsData) return 0;
    return productsData.totalPages || Math.ceil((productsData.totalElements || products.length) / pageSize) || 1;
  }, [productsData, products.length, pageSize]);

  const isLoading = categoryLoading || productsLoading;
  const hasError = categoryError || productsError;

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <LoadingSpinner />
      </div>
    );
  }

  if (hasError || !category) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-16">
          <div className="text-red-600 text-lg font-semibold mb-2">
            {!category ? 'Category not found' : 'Error loading category'}
          </div>
          <div className="text-gray-600">
            {!category ? 'The category you\'re looking for doesn\'t exist.' : 'Please try again later'}
          </div>
        </div>
      </div>
    );
  }

  const handleSortChange = (newSort: string) => {
    setSortBy(newSort);
    setCurrentPage(0); // Reset to first page when sorting changes
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Breadcrumb */}
      <div className="mb-6">
        <CategoryBreadcrumb category={category} />
      </div>

      {/* Category Header */}
      <div className="mb-8">
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <h1 className="text-3xl font-bold text-gray-900 mb-2">{category.name}</h1>
            {category.description && (
              <p className="text-gray-600 max-w-3xl">{category.description}</p>
            )}
            <div className="mt-2 text-sm text-gray-500">
              {products.length} {products.length === 1 ? 'product' : 'products'} found
            </div>
          </div>
        </div>
      </div>

      {/* Subcategories */}
      {subcategories && subcategories.length > 0 && (
        <div className="mb-8">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">Subcategories</h2>
          <SubcategoryList
            categories={subcategories}
            layout="horizontal"
            showProductCount={true}
          />
        </div>
      )}

      {/* Filters and Sort */}
      <div className="mb-6 flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between border-b pb-4">
        {/* Filter Button */}
        <button
          onClick={() => setShowFilters(!showFilters)}
          className="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
        >
          <FunnelIcon className="h-4 w-4 mr-2" />
          Filters
        </button>

        {/* Sort Dropdown */}
        <div className="flex items-center space-x-4">
          <span className="text-sm text-gray-600">Sort by:</span>
          <select
            value={sortBy}
            onChange={(e) => handleSortChange(e.target.value)}
            className="border border-gray-300 rounded-md px-3 py-1 text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="featured">Featured</option>
            <option value="name,asc">Name (A-Z)</option>
            <option value="name,desc">Name (Z-A)</option>
            <option value="price,asc">Price (Low to High)</option>
            <option value="price,desc">Price (High to Low)</option>
            <option value="createdAt,desc">Newest First</option>
            <option value="createdAt,asc">Oldest First</option>
          </select>
        </div>
      </div>

      {/* Filters Panel (if shown) */}
      {showFilters && (
        <div className="mb-6 bg-gray-50 border rounded-lg p-4">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <h4 className="font-medium text-gray-900 mb-2">Price Range</h4>
              <div className="space-y-2 text-sm">
                <label className="flex items-center">
                  <input type="checkbox" className="rounded border-gray-300 text-blue-600" />
                  <span className="ml-2">Under $25</span>
                </label>
                <label className="flex items-center">
                  <input type="checkbox" className="rounded border-gray-300 text-blue-600" />
                  <span className="ml-2">$25 - $50</span>
                </label>
                <label className="flex items-center">
                  <input type="checkbox" className="rounded border-gray-300 text-blue-600" />
                  <span className="ml-2">$50 - $100</span>
                </label>
                <label className="flex items-center">
                  <input type="checkbox" className="rounded border-gray-300 text-blue-600" />
                  <span className="ml-2">Over $100</span>
                </label>
              </div>
            </div>
            
            <div>
              <h4 className="font-medium text-gray-900 mb-2">Availability</h4>
              <div className="space-y-2 text-sm">
                <label className="flex items-center">
                  <input type="checkbox" className="rounded border-gray-300 text-blue-600" />
                  <span className="ml-2">In Stock</span>
                </label>
                <label className="flex items-center">
                  <input type="checkbox" className="rounded border-gray-300 text-blue-600" />
                  <span className="ml-2">On Sale</span>
                </label>
              </div>
            </div>

            <div>
              <h4 className="font-medium text-gray-900 mb-2">Rating</h4>
              <div className="space-y-2 text-sm">
                <label className="flex items-center">
                  <input type="checkbox" className="rounded border-gray-300 text-blue-600" />
                  <span className="ml-2">4+ Stars</span>
                </label>
                <label className="flex items-center">
                  <input type="checkbox" className="rounded border-gray-300 text-blue-600" />
                  <span className="ml-2">3+ Stars</span>
                </label>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Products Grid */}
      {productsLoading ? (
        <div className="flex justify-center items-center min-h-[200px]">
          <LoadingSpinner />
        </div>
      ) : products.length === 0 ? (
        <div className="text-center py-16">
          <div className="text-gray-500 text-lg mb-2">No products found</div>
          <div className="text-gray-400">
            Try adjusting your filters or check back later for new products.
          </div>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {products.map((product: Product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="mt-8 flex justify-center">
              <nav className="flex space-x-2">
                <button
                  onClick={() => handlePageChange(Math.max(0, currentPage - 1))}
                  disabled={currentPage === 0}
                  className="px-3 py-2 border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Previous
                </button>
                {[...Array(Math.min(totalPages, 5))].map((_, index) => {
                  const pageNumber = Math.max(0, Math.min(totalPages - 5, currentPage - 2)) + index;
                  return (
                    <button
                      key={pageNumber}
                      onClick={() => handlePageChange(pageNumber)}
                      className={`px-3 py-2 border rounded-md ${
                        currentPage === pageNumber
                          ? 'bg-blue-600 text-white border-blue-600'
                          : 'border-gray-300 hover:bg-gray-50'
                      }`}
                    >
                      {pageNumber + 1}
                    </button>
                  );
                })}
                <button
                  onClick={() => handlePageChange(Math.min(totalPages - 1, currentPage + 1))}
                  disabled={currentPage >= totalPages - 1}
                  className="px-3 py-2 border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
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