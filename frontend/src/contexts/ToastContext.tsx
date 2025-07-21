import React, { createContext, useContext, useState, ReactNode } from 'react';
import Toast from '@/components/ui/Toast';

interface ToastItem {
  id: string;
  message: string;
  type?: 'success' | 'error' | 'info';
  duration?: number;
}

/* eslint-disable no-unused-vars */
interface ToastContextType {
  showToast: (message: string, type?: 'success' | 'error' | 'info', duration?: number) => void;
  showSuccess: (message: string, duration?: number) => void;
  showError: (message: string, duration?: number) => void;
  showInfo: (message: string, duration?: number) => void;
}
/* eslint-enable no-unused-vars */

const ToastContext = createContext<ToastContextType | undefined>(undefined);

export const useToast = () => {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast must be used within a ToastProvider');
  }
  return context;
};

interface ToastProviderProps {
  children: ReactNode;
}

export const ToastProvider: React.FC<ToastProviderProps> = ({ children }) => {
  const [toasts, setToasts] = useState<ToastItem[]>([]);

  const showToast = (message: string, type: 'success' | 'error' | 'info' = 'success', duration = 3000) => {
    const id = Date.now().toString();
    const toast: ToastItem = { id, message, type, duration };
    
    setToasts(prev => [...prev, toast]);
  };

  const showSuccess = (message: string, duration = 3000) => {
    showToast(message, 'success', duration);
  };

  const showError = (message: string, duration = 4000) => {
    showToast(message, 'error', duration);
  };

  const showInfo = (message: string, duration = 3000) => {
    showToast(message, 'info', duration);
  };

  const removeToast = (id: string) => {
    setToasts(prev => prev.filter(toast => toast.id !== id));
  };

  return (
    <ToastContext.Provider value={{ showToast, showSuccess, showError, showInfo }}>
      {children}
      
      {/* Render Toasts */}
      <div className="fixed top-4 right-4 space-y-2 z-50">
        {toasts.map((toast, index) => (
          <div 
            key={toast.id} 
            style={{ 
              marginTop: `${index * 80}px`,
              position: 'relative',
              zIndex: 50 - index
            }}
          >
            <Toast
              message={toast.message}
              type={toast.type}
              duration={toast.duration}
              onClose={() => removeToast(toast.id)}
            />
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
};