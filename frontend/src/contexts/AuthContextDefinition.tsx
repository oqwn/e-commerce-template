import { createContext } from 'react';

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: 'BUYER' | 'SELLER' | 'ADMIN';
  emailVerified: boolean;
  phoneNumber?: string;
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';
  createdAt: string;
}

export interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  // eslint-disable-next-line no-unused-vars
  login: (email: string, password: string) => Promise<void>;
  // eslint-disable-next-line no-unused-vars
  register: (data: RegisterData) => Promise<void>;
  logout: () => void;
  refreshToken: () => Promise<void>;
  // eslint-disable-next-line no-unused-vars
  updateUser: (updatedUser: User) => void;
}

export interface RegisterData {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role?: 'BUYER' | 'SELLER';
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);