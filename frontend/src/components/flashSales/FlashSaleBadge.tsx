import React from 'react';
import { Flame } from 'lucide-react';

interface FlashSaleBadgeProps {
  discountPercentage?: number;
  className?: string;
  size?: 'sm' | 'md' | 'lg';
}

export const FlashSaleBadge: React.FC<FlashSaleBadgeProps> = ({
  discountPercentage,
  className = '',
  size = 'md'
}) => {
  const sizeClasses = {
    sm: 'px-1.5 py-0.5 text-xs',
    md: 'px-2 py-1 text-sm', 
    lg: 'px-3 py-1.5 text-base'
  };

  return (
    <div className={`
      inline-flex items-center space-x-1 bg-gradient-to-r from-red-500 to-pink-500 
      text-white font-bold rounded-full shadow-sm animate-pulse
      ${sizeClasses[size]} ${className}
    `}>
      <Flame className="w-3 h-3" />
      <span>
        {discountPercentage ? `${discountPercentage}% OFF` : 'FLASH SALE'}
      </span>
    </div>
  );
};