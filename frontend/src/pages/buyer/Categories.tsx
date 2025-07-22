import { useState, useMemo } from 'react';
import { MagnifyingGlassIcon, Squares2X2Icon, ListBulletIcon } from '@heroicons/react/24/outline';
import { CategoryCard } from '@/components/categories/CategoryCard';
import { SubcategoryList } from '@/components/categories/SubcategoryList';
import { CategoryBreadcrumb } from '@/components/categories/CategoryBreadcrumb';
import { useRootCategories, useCategories } from '@/hooks/useCategories';
import LoadingSpinner from '@/components/common/LoadingSpinner';

type ViewMode = 'grid' | 'list';

export default function Categories() {
  const [searchTerm, setSearchTerm] = useState('');
  const [viewMode, setViewMode] = useState<ViewMode>('grid');
  
  const { data: rootCategories, isLoading: isLoadingRoot, error: rootError } = useRootCategories();
  const { data: allCategories, isLoading: isLoadingAll, error: allError } = useCategories();

  // Filter categories based on search term
  const filteredCategories = useMemo(() => {
    if (!rootCategories) return [];
    
    if (!searchTerm.trim()) {
      return rootCategories;
    }

    // Search in all categories and return matches
    const searchLower = searchTerm.toLowerCase();
    return allCategories?.filter((category: any) => 
      category.name.toLowerCase().includes(searchLower) ||
      category.description?.toLowerCase().includes(searchLower)
    ) || [];
  }, [rootCategories, allCategories, searchTerm]);

  if (isLoadingRoot || isLoadingAll) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <LoadingSpinner />
      </div>
    );
  }

  if (rootError || allError) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-16">
          <div className="text-red-600 text-lg font-semibold mb-2">Error loading categories</div>
          <div className="text-gray-600">Please try again later</div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Breadcrumb */}
      <div className="mb-6">
        <CategoryBreadcrumb />
      </div>

      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Browse Categories</h1>
        <p className="text-gray-600">
          Discover products across all our categories. Find exactly what you're looking for.
        </p>
      </div>

      {/* Search and View Controls */}
      <div className="mb-8 flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between">
        {/* Search Bar */}
        <div className="relative flex-1 max-w-md">
          <MagnifyingGlassIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
          <input
            type="text"
            placeholder="Search categories..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          />
        </div>

        {/* View Mode Toggle */}
        <div className="flex items-center space-x-2">
          <span className="text-sm text-gray-600">View:</span>
          <button
            onClick={() => setViewMode('grid')}
            className={`p-2 rounded-md transition-colors ${
              viewMode === 'grid'
                ? 'bg-blue-100 text-blue-600'
                : 'text-gray-500 hover:bg-gray-100'
            }`}
          >
            <Squares2X2Icon className="h-5 w-5" />
          </button>
          <button
            onClick={() => setViewMode('list')}
            className={`p-2 rounded-md transition-colors ${
              viewMode === 'list'
                ? 'bg-blue-100 text-blue-600'
                : 'text-gray-500 hover:bg-gray-100'
            }`}
          >
            <ListBulletIcon className="h-5 w-5" />
          </button>
        </div>
      </div>

      {/* Results Count */}
      <div className="mb-6">
        <p className="text-sm text-gray-600">
          {searchTerm ? (
            <>
              {filteredCategories.length} {filteredCategories.length === 1 ? 'category' : 'categories'} 
              found for "{searchTerm}"
            </>
          ) : (
            <>Showing {filteredCategories.length} main {filteredCategories.length === 1 ? 'category' : 'categories'}</>
          )}
        </p>
      </div>

      {/* Categories Display */}
      {filteredCategories.length === 0 ? (
        <div className="text-center py-16">
          <div className="text-gray-500 text-lg mb-2">
            {searchTerm ? 'No categories found' : 'No categories available'}
          </div>
          {searchTerm && (
            <div className="text-gray-400">
              Try adjusting your search terms or browse all categories.
            </div>
          )}
        </div>
      ) : viewMode === 'grid' ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {filteredCategories.map((category: any) => (
            <CategoryCard 
              key={category.id} 
              category={category} 
              showChildren={!searchTerm} // Don't show children in search results
            />
          ))}
        </div>
      ) : (
        <div className="bg-white rounded-lg shadow-sm border">
          <SubcategoryList
            categories={filteredCategories}
            layout="vertical"
            showProductCount={true}
            className="divide-y divide-gray-200"
          />
        </div>
      )}

      {/* Tips Section */}
      {!searchTerm && filteredCategories.length > 0 && (
        <div className="mt-12 bg-blue-50 rounded-lg p-6">
          <h3 className="text-lg font-semibold text-blue-900 mb-3">Shopping Tips</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <h4 className="font-medium text-blue-800 mb-1">Browse by Category</h4>
              <p className="text-blue-700 text-sm">
                Click on any category to see all products and subcategories within it.
              </p>
            </div>
            <div>
              <h4 className="font-medium text-blue-800 mb-1">Use Search</h4>
              <p className="text-blue-700 text-sm">
                Search for specific categories to quickly find what you're looking for.
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}