import React, { useRef, useState } from 'react';
import { ShoppingCartIcon } from '@heroicons/react/24/outline';
import { CheckIcon } from '@heroicons/react/24/solid';
import { useCart } from '@/contexts/useCart';
import { useAddToCartAnimation } from '@/hooks/useAddToCartAnimation';
import { toast } from 'react-toastify';

interface AnimatedAddToCartButtonProps {
  productId: number;
  productName: string;
  productImageUrl?: string;
  className?: string;
  variant?: 'primary' | 'secondary';
  size?: 'sm' | 'md' | 'lg';
  disabled?: boolean;
  showSuccessState?: boolean;
}

const AnimatedAddToCartButton: React.FC<AnimatedAddToCartButtonProps> = ({
  productId,
  productName,
  productImageUrl,
  className = '',
  variant = 'primary',
  size = 'md',
  disabled = false,
  showSuccessState = true
}) => {
  const buttonRef = useRef<HTMLButtonElement>(null);
  const [isAdding, setIsAdding] = useState(false);
  const [showSuccess, setShowSuccess] = useState(false);
  const { addToCart } = useCart();
  const { animateAddToCart } = useAddToCartAnimation();

  const baseClasses = "flex items-center justify-center font-medium rounded-lg transition-all duration-200 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-offset-2";
  
  const variantClasses = {
    primary: "bg-blue-600 text-white hover:bg-blue-700 focus:ring-blue-500 disabled:bg-blue-300",
    secondary: "bg-white text-blue-600 border-2 border-blue-600 hover:bg-blue-50 focus:ring-blue-500 disabled:bg-gray-100 disabled:text-gray-400 disabled:border-gray-300"
  };

  const sizeClasses = {
    sm: "px-3 py-1.5 text-sm",
    md: "px-4 py-2",
    lg: "px-6 py-3 text-lg"
  };

  const iconSizes = {
    sm: "h-4 w-4",
    md: "h-5 w-5",
    lg: "h-6 w-6"
  };

  const handleAddToCart = async () => {
    if (!buttonRef.current || isAdding || disabled) return;

    console.log('🛒 Add to cart clicked for:', productName);

    try {
      setIsAdding(true);

      // Start animation (always try animation, even without image)
      console.log('🎭 Starting animation for product:', productName);
      animateAddToCart({
        productImageUrl,
        productName,
        sourceElement: buttonRef.current,
        onComplete: () => {
          console.log('🎉 Animation completed for:', productName);
          if (showSuccessState) {
            setShowSuccess(true);
            toast.success(`${productName} added to cart!`, {
              position: "top-right",
              autoClose: 2000,
              hideProgressBar: false,
              closeOnClick: true,
              pauseOnHover: true,
              draggable: true,
            });
            setTimeout(() => setShowSuccess(false), 2000);
          }
        }
      });

      // Add to cart
      console.log('📦 Adding to cart via API...');
      await addToCart(productId, 1);
      console.log('✅ Successfully added to cart');

    } catch (error) {
      console.error('❌ Error adding to cart:', error);
    } finally {
      setIsAdding(false);
    }
  };

  const buttonClass = `${baseClasses} ${variantClasses[variant]} ${sizeClasses[size]} ${className}`;

  if (showSuccess && showSuccessState) {
    return (
      <button
        ref={buttonRef}
        disabled={true}
        className={`${buttonClass} bg-green-600 text-white success-pulse`}
      >
        <CheckIcon className={`${iconSizes[size]} mr-2`} />
        Added!
      </button>
    );
  }

  return (
    <button
      ref={buttonRef}
      onClick={handleAddToCart}
      disabled={disabled || isAdding}
      className={buttonClass}
    >
      {isAdding ? (
        <>
          <svg 
            className={`animate-spin ${iconSizes[size]} mr-2`} 
            xmlns="http://www.w3.org/2000/svg" 
            fill="none" 
            viewBox="0 0 24 24"
          >
            <circle 
              className="opacity-25" 
              cx="12" 
              cy="12" 
              r="10" 
              stroke="currentColor" 
              strokeWidth="4"
            />
            <path 
              className="opacity-75" 
              fill="currentColor" 
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
            />
          </svg>
          Adding...
        </>
      ) : (
        <>
          <ShoppingCartIcon className={`${iconSizes[size]} mr-2`} />
          Add to Cart
        </>
      )}
    </button>
  );
};

export default AnimatedAddToCartButton;