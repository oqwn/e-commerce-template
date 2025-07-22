import { useState, useEffect, useCallback } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { wishlistApi } from '@/api/wishlist';
import { useAuth } from '@/contexts/useAuth';
import { toast } from 'react-hot-toast';

export const useWishlist = () => {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [wishlistProductIds, setWishlistProductIds] = useState<Set<number>>(new Set());

  // Fetch wishlist product IDs
  const { data: productIdsData, isLoading: isLoadingIds } = useQuery({
    queryKey: ['wishlist-product-ids'],
    queryFn: async () => {
      const response = await wishlistApi.getWishlistProductIds();
      return response.data || [];
    },
    enabled: !!user && user.role === 'BUYER',
    staleTime: 5 * 60 * 1000, // 5 minutes
  });

  // Update local state when product IDs are fetched
  useEffect(() => {
    if (productIdsData) {
      setWishlistProductIds(new Set(productIdsData));
    }
  }, [productIdsData]);

  // Fetch wishlist count
  const { data: wishlistCount } = useQuery({
    queryKey: ['wishlist-count'],
    queryFn: async () => {
      const response = await wishlistApi.getWishlistCount();
      return response.data || 0;
    },
    enabled: !!user && user.role === 'BUYER',
  });

  // Add to wishlist mutation
  const addToWishlistMutation = useMutation({
    mutationFn: wishlistApi.addToWishlist,
    onSuccess: (_, variables) => {
      setWishlistProductIds(prev => new Set(prev).add(variables.productId));
      queryClient.invalidateQueries({ queryKey: ['wishlist-product-ids'] });
      queryClient.invalidateQueries({ queryKey: ['wishlist-count'] });
      queryClient.invalidateQueries({ queryKey: ['wishlist'] });
      toast.success('Added to wishlist');
    },
    onError: () => {
      toast.error('Failed to add to wishlist');
    },
  });

  // Remove from wishlist mutation
  const removeFromWishlistMutation = useMutation({
    mutationFn: wishlistApi.removeFromWishlist,
    onSuccess: (_, productId) => {
      setWishlistProductIds(prev => {
        const newSet = new Set(prev);
        newSet.delete(productId);
        return newSet;
      });
      queryClient.invalidateQueries({ queryKey: ['wishlist-product-ids'] });
      queryClient.invalidateQueries({ queryKey: ['wishlist-count'] });
      queryClient.invalidateQueries({ queryKey: ['wishlist'] });
      toast.success('Removed from wishlist');
    },
    onError: () => {
      toast.error('Failed to remove from wishlist');
    },
  });

  // Toggle wishlist
  const toggleWishlist = useCallback(
    async (productId: number) => {
      if (!user || user.role !== 'BUYER') {
        toast.error('Please log in as a buyer to add items to wishlist');
        return;
      }

      const isInWishlist = wishlistProductIds.has(productId);
      
      if (isInWishlist) {
        removeFromWishlistMutation.mutate(productId);
      } else {
        addToWishlistMutation.mutate({ productId });
      }
    },
    [user, wishlistProductIds, addToWishlistMutation, removeFromWishlistMutation]
  );

  // Check if product is in wishlist
  const isInWishlist = useCallback(
    (productId: number) => {
      return wishlistProductIds.has(productId);
    },
    [wishlistProductIds]
  );

  return {
    wishlistProductIds,
    wishlistCount: wishlistCount || 0,
    isLoadingIds,
    isInWishlist,
    toggleWishlist,
    isTogglingWishlist: addToWishlistMutation.isPending || removeFromWishlistMutation.isPending,
  };
};

// Hook for fetching paginated wishlist items
export const useWishlistItems = (page = 0, size = 12) => {
  const { user } = useAuth();

  return useQuery({
    queryKey: ['wishlist', page, size],
    queryFn: async () => {
      const response = await wishlistApi.getWishlist(page, size);
      return response.data;
    },
    enabled: !!user && user.role === 'BUYER',
  });
};