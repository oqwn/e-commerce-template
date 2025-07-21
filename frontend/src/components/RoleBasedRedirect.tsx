import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '@/contexts/useAuth';
import LoadingSpinner from '@/components/common/LoadingSpinner';

const RoleBasedRedirect: React.FC = () => {
  const { user, isLoading, isAuthenticated } = useAuth();

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // Redirect based on user role
  switch (user?.role) {
    case 'BUYER':
      return <Navigate to="/buyer" replace />;
    case 'SELLER':
      return <Navigate to="/seller" replace />;
    case 'ADMIN':
      return <Navigate to="/admin" replace />;
    default:
      return <Navigate to="/unauthorized" replace />;
  }
};

export default RoleBasedRedirect;