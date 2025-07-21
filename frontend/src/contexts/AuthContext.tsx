import React, { useState, useEffect, useCallback } from 'react';
import { api } from '@/services/api';
import { AuthContext, User, RegisterData } from './AuthContextDefinition';

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  const logout = useCallback(() => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    setUser(null);
    setIsAuthenticated(false);
    api.clearAuthToken();
  }, []);

  const refreshToken = useCallback(async () => {
    try {
      const refreshTokenValue = localStorage.getItem('refreshToken');
      if (!refreshTokenValue) {
        throw new Error('No refresh token');
      }

      const response = await api.post('/auth/refresh', { refreshToken: refreshTokenValue });
      
      if (response.success) {
        const { accessToken, refreshToken: newRefreshToken, user } = response.data;
        
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', newRefreshToken);
        
        setUser(user);
        setIsAuthenticated(true);
        
        api.setAuthToken(accessToken);
      } else {
        throw new Error('Token refresh failed');
      }
    } catch (error) {
      console.error('Token refresh failed:', error);
      logout();
    }
  }, [logout]);

  // Load user from token on mount
  useEffect(() => {
    const loadUser = async () => {
      const token = localStorage.getItem('accessToken');
      if (token) {
        try {
          // Decode token to get user info
          const payload = JSON.parse(atob(token.split('.')[1]));
          if (payload.exp * 1000 > Date.now()) {
            setUser({
              id: payload.userId,
              email: payload.email,
              firstName: payload.firstName,
              lastName: payload.lastName,
              role: payload.role,
              emailVerified: payload.emailVerified,
              phoneNumber: payload.phoneNumber,
              status: payload.status || 'ACTIVE',
              createdAt: payload.createdAt || new Date().toISOString(),
            });
            setIsAuthenticated(true);
            // Set the auth token for API requests
            api.setAuthToken(token);
          } else {
            // Token expired, try to refresh
            await refreshToken();
          }
        } catch (error) {
          console.error('Error loading user:', error);
          logout();
        }
      }
      setIsLoading(false);
    };

    loadUser();
  }, [refreshToken, logout]);

  const login = async (email: string, password: string) => {
    try {
      const response = await api.post('/auth/login', { email, password });
      
      if (response.success) {
        const { accessToken, refreshToken, user } = response.data;
        
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
        
        setUser(user);
        setIsAuthenticated(true);
        
        // Set up API interceptor for auth header
        api.setAuthToken(accessToken);
      } else {
        throw new Error(response.message || 'Login failed');
      }
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Login failed');
    }
  };

  const register = async (data: RegisterData) => {
    try {
      const response = await api.post('/auth/register', data);
      
      if (!response.success) {
        throw new Error(response.message || 'Registration failed');
      }
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Registration failed');
    }
  };


  const updateUser = (updatedUser: User) => {
    setUser(updatedUser);
  };

  const value = {
    user,
    isAuthenticated,
    isLoading,
    login,
    register,
    logout,
    refreshToken,
    updateUser,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};