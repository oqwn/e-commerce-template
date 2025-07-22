import { api } from '@/services/api';
import { Category } from '@/types';

export interface CategoryResponse {
  success: boolean;
  message: string;
  data: Category | Category[];
}

export interface CategoriesPageResponse {
  content: Category[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}

// Categories API functions
export const categoriesApi = {
  // Get all categories
  getAllCategories: () =>
    api.get('/categories'),

  // Get root categories only (no parent)
  getRootCategories: () =>
    api.get('/categories/root'),

  // Get children of a specific category
  getChildCategories: (parentId: number) =>
    api.get(`/categories/${parentId}/children`),

  // Get category by slug
  getCategoryBySlug: (slug: string) =>
    api.get(`/categories/slug/${slug}`),

  // Get category by ID
  getCategoryById: (id: number) =>
    api.get(`/categories/${id}`),

  // Create new category (seller/admin)
  createCategory: (data: Partial<Category>) =>
    api.post('/categories', data),

  // Update category (seller/admin)  
  updateCategory: (id: number, data: Partial<Category>) =>
    api.put(`/categories/${id}`, data),

  // Delete category (seller/admin)
  deleteCategory: (id: number) =>
    api.delete(`/categories/${id}`),

  // Get products by category
  getProductsByCategory: (slug: string, params?: { page?: number; size?: number; sort?: string }) => {
    const queryString = params ? 
      '?' + Object.entries(params)
        .filter(([_, value]) => value !== undefined)
        .map(([key, value]) => `${key}=${encodeURIComponent(value)}`)
        .join('&') 
      : '';
    return api.get(`/products/category/${slug}${queryString}`);
  },
};

// React Query keys for caching
export const categoryKeys = {
  all: ['categories'] as const,
  lists: () => [...categoryKeys.all, 'list'] as const,
  list: (filters: Record<string, any>) => [...categoryKeys.lists(), { filters }] as const,
  details: () => [...categoryKeys.all, 'detail'] as const,
  detail: (id: string | number) => [...categoryKeys.details(), id] as const,
  root: () => [...categoryKeys.all, 'root'] as const,
  children: (parentId: number) => [...categoryKeys.all, 'children', parentId] as const,
  bySlug: (slug: string) => [...categoryKeys.all, 'slug', slug] as const,
  products: (slug: string, params?: any) => ['products', 'category', slug, params] as const,
};