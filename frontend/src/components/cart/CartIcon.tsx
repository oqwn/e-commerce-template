import React from 'react';
import { ShoppingCartIcon } from '@heroicons/react/24/outline';
import { useNavigate } from 'react-router-dom';
import { useCart } from '@/contexts/useCart';

const CartIcon: React.FC = () => {
  const navigate = useNavigate();
  const { cartItemCount } = useCart();

  return (
    <button
      onClick={() => navigate('/cart')}
      className="relative p-2 text-gray-600 hover:text-gray-900 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
    >
      <ShoppingCartIcon className="h-6 w-6" />
      {cartItemCount > 0 && (
        <span className="absolute -top-1 -right-1 h-5 w-5 rounded-full bg-indigo-600 flex items-center justify-center">
          <span className="text-xs font-medium text-white">
            {cartItemCount > 99 ? '99+' : cartItemCount}
          </span>
        </span>
      )}
    </button>
  );
};

export default CartIcon;