import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { categoriesApi, categoryKeys } from '@/api/categories';
import { Category } from '@/types';

// Get all categories
export const useCategories = () => {
  return useQuery({
    queryKey: categoryKeys.lists(),
    queryFn: async () => {
      const response = await categoriesApi.getAllCategories();
      return response.data.data || response.data || [];
    },
    staleTime: 5 * 60 * 1000, // 5 minutes
    gcTime: 10 * 60 * 1000, // 10 minutes
  });
};

// Get root categories
export const useRootCategories = () => {
  return useQuery({
    queryKey: categoryKeys.root(),
    queryFn: async () => {
      const response = await categoriesApi.getRootCategories();
      return response.data.data || response.data || [];
    },
    staleTime: 5 * 60 * 1000, // 5 minutes
    gcTime: 10 * 60 * 1000, // 10 minutes
  });
};

// Get child categories
export const useChildCategories = (parentId: number | null) => {
  return useQuery({
    queryKey: categoryKeys.children(parentId!),
    queryFn: async () => {
      const response = await categoriesApi.getChildCategories(parentId!);
      return response.data.data || response.data || [];
    },
    enabled: !!parentId,
    staleTime: 5 * 60 * 1000, // 5 minutes
    gcTime: 10 * 60 * 1000, // 10 minutes
  });
};

// Get category by slug
export const useCategoryBySlug = (slug: string | null) => {
  return useQuery({
    queryKey: categoryKeys.bySlug(slug!),
    queryFn: async () => {
      const response = await categoriesApi.getCategoryBySlug(slug!);
      return response.data.data || response.data;
    },
    enabled: !!slug,
    staleTime: 5 * 60 * 1000, // 5 minutes
    gcTime: 10 * 60 * 1000, // 10 minutes
  });
};

// Get category by ID
export const useCategoryById = (id: number | null) => {
  return useQuery({
    queryKey: categoryKeys.detail(id!),
    queryFn: async () => {
      const response = await categoriesApi.getCategoryById(id!);
      return response.data.data || response.data;
    },
    enabled: !!id,
    staleTime: 5 * 60 * 1000, // 5 minutes
    gcTime: 10 * 60 * 1000, // 10 minutes
  });
};

// Get products by category
export const useProductsByCategory = (slug: string | null, params?: { page?: number; size?: number; sort?: string }) => {
  return useQuery({
    queryKey: categoryKeys.products(slug!, params),
    queryFn: async () => {
      const response = await categoriesApi.getProductsByCategory(slug!, params);
      return response.data.data || response.data;
    },
    enabled: !!slug,
    staleTime: 2 * 60 * 1000, // 2 minutes
    gcTime: 5 * 60 * 1000, // 5 minutes
  });
};

// Create category mutation
export const useCreateCategory = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: Partial<Category>) => categoriesApi.createCategory(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: categoryKeys.all });
    },
  });
};

// Update category mutation
export const useUpdateCategory = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: Partial<Category> }) => 
      categoriesApi.updateCategory(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: categoryKeys.all });
      queryClient.invalidateQueries({ queryKey: categoryKeys.detail(id) });
    },
  });
};

// Delete category mutation
export const useDeleteCategory = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (id: number) => categoriesApi.deleteCategory(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: categoryKeys.all });
    },
  });
};