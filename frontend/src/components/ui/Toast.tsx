import React, { useEffect } from 'react';
import { CheckCircleIcon, XMarkIcon } from '@heroicons/react/24/solid';

interface ToastProps {
  message: string;
  type?: 'success' | 'error' | 'info';
  duration?: number;
  onClose: () => void;
}

const Toast: React.FC<ToastProps> = ({ 
  message, 
  type = 'success', 
  duration = 3000, 
  onClose 
}) => {
  useEffect(() => {
    const timer = setTimeout(onClose, duration);
    return () => clearTimeout(timer);
  }, [duration, onClose]);

  const typeClasses = {
    success: 'bg-green-50 border-green-200 text-green-800',
    error: 'bg-red-50 border-red-200 text-red-800',
    info: 'bg-blue-50 border-blue-200 text-blue-800'
  };

  const iconClasses = {
    success: 'text-green-400',
    error: 'text-red-400',
    info: 'text-blue-400'
  };

  return (
    <div className={`fixed top-4 right-4 max-w-sm w-full bg-white rounded-lg shadow-lg border ${typeClasses[type]} p-4 z-50 animate-in slide-in-from-right`}>
      <div className="flex items-start">
        <div className="flex-shrink-0">
          <CheckCircleIcon className={`h-5 w-5 ${iconClasses[type]}`} />
        </div>
        <div className="ml-3 flex-1">
          <p className="text-sm font-medium">{message}</p>
        </div>
        <div className="ml-4 flex-shrink-0">
          <button
            onClick={onClose}
            className="inline-flex text-gray-400 hover:text-gray-600 focus:outline-none"
          >
            <XMarkIcon className="h-4 w-4" />
          </button>
        </div>
      </div>
    </div>
  );
};

export default Toast;