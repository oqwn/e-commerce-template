import { useState, useCallback, useEffect } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { flashSalesApi } from '@/api/flashSales';
import { toast } from 'react-hot-toast';

export const useActiveFlashSales = () => {
  return useQuery({
    queryKey: ['flash-sales', 'active'],
    queryFn: async () => {
      const response = await flashSalesApi.getActiveFlashSales();
      return response.data.data || [];
    },
    staleTime: 30 * 1000, // 30 seconds
    refetchInterval: 60 * 1000, // Refetch every minute
  });
};

export const useUpcomingFlashSales = () => {
  return useQuery({
    queryKey: ['flash-sales', 'upcoming'],
    queryFn: async () => {
      const response = await flashSalesApi.getUpcomingFlashSales();
      return response.data.data || [];
    },
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
};

export const useActiveFlashSaleProducts = () => {
  return useQuery({
    queryKey: ['flash-sale-products', 'active'],
    queryFn: async () => {
      const response = await flashSalesApi.getActiveFlashSaleProducts();
      return response.data.data;
    },
    staleTime: 30 * 1000, // 30 seconds
    refetchInterval: 60 * 1000, // Refetch every minute
  });
};

export const useFlashSaleByProduct = (productId: number | null) => {
  return useQuery({
    queryKey: ['flash-sales', 'product', productId],
    queryFn: async () => {
      if (!productId) return null;
      const response = await flashSalesApi.getFlashSaleByProduct(productId);
      return response.data.data;
    },
    enabled: !!productId,
    staleTime: 30 * 1000, // 30 seconds
    refetchInterval: 60 * 1000, // Refetch every minute
  });
};

export const useMyFlashSales = () => {
  return useQuery({
    queryKey: ['flash-sales', 'my'],
    queryFn: async () => {
      const response = await flashSalesApi.getMyFlashSales();
      return response.data.data;
    },
    staleTime: 2 * 60 * 1000, // 2 minutes
  });
};

export const useFlashSaleById = (id: number | null) => {
  return useQuery({
    queryKey: ['flash-sales', id],
    queryFn: async () => {
      if (!id) return null;
      const response = await flashSalesApi.getFlashSale(id);
      return response.data.data;
    },
    enabled: !!id,
    staleTime: 60 * 1000, // 1 minute
  });
};

export const useFlashSaleMutations = () => {
  const queryClient = useQueryClient();

  const createMutation = useMutation({
    mutationFn: flashSalesApi.createFlashSale,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['flash-sales'] });
      toast.success('Flash sale created successfully');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to create flash sale');
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: any }) =>
      flashSalesApi.updateFlashSale(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['flash-sales'] });
      toast.success('Flash sale updated successfully');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to update flash sale');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: flashSalesApi.deleteFlashSale,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['flash-sales'] });
      toast.success('Flash sale deleted successfully');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to delete flash sale');
    },
  });

  const activateMutation = useMutation({
    mutationFn: flashSalesApi.activateFlashSale,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['flash-sales'] });
      toast.success('Flash sale activated');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to activate flash sale');
    },
  });

  const deactivateMutation = useMutation({
    mutationFn: flashSalesApi.deactivateFlashSale,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['flash-sales'] });
      toast.success('Flash sale deactivated');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to deactivate flash sale');
    },
  });

  return {
    createFlashSale: createMutation.mutate,
    updateFlashSale: updateMutation.mutate,
    deleteFlashSale: deleteMutation.mutate,
    activateFlashSale: activateMutation.mutate,
    deactivateFlashSale: deactivateMutation.mutate,
    isLoading: createMutation.isPending || updateMutation.isPending || 
               deleteMutation.isPending || activateMutation.isPending || 
               deactivateMutation.isPending,
  };
};

// Utility function to calculate time remaining
export const useTimeRemaining = (endTime: string) => {
  const [timeRemaining, setTimeRemaining] = useState<{
    days: number;
    hours: number;
    minutes: number;
    seconds: number;
    expired: boolean;
  }>({ days: 0, hours: 0, minutes: 0, seconds: 0, expired: false });

  const calculateTimeRemaining = useCallback(() => {
    const end = new Date(endTime).getTime();
    const now = new Date().getTime();
    const difference = end - now;

    if (difference > 0) {
      const days = Math.floor(difference / (1000 * 60 * 60 * 24));
      const hours = Math.floor((difference % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
      const minutes = Math.floor((difference % (1000 * 60 * 60)) / (1000 * 60));
      const seconds = Math.floor((difference % (1000 * 60)) / 1000);

      setTimeRemaining({ days, hours, minutes, seconds, expired: false });
    } else {
      setTimeRemaining({ days: 0, hours: 0, minutes: 0, seconds: 0, expired: true });
    }
  }, [endTime]);

  useEffect(() => {
    calculateTimeRemaining();
    const interval = setInterval(calculateTimeRemaining, 1000);
    return () => clearInterval(interval);
  }, [calculateTimeRemaining]);

  return timeRemaining;
};