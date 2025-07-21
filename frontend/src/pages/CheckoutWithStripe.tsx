import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { loadStripe } from '@stripe/stripe-js';
import { Elements } from '@stripe/react-stripe-js';
import { useCart } from '@/contexts/useCart';
// import { useAuth } from '@/contexts/useAuth'; // Will be used later for user validation
import api from '@/services/api';
import paymentService from '@/services/paymentService';
import StripePaymentForm from '@/components/payment/StripePaymentForm';
import { toast } from 'react-toastify';
import { 
  MapPinIcon, 
  CreditCardIcon, 
  TruckIcon,
  PlusIcon,
  CheckCircleIcon
} from '@heroicons/react/24/outline';

// Initialize Stripe - in production, use environment variable
const stripePromise = loadStripe((import.meta as any).env?.VITE_STRIPE_PUBLISHABLE_KEY || 'pk_test_YOUR_STRIPE_PUBLISHABLE_KEY');

interface Address {
  id: number;
  name: string;
  phone: string;
  street: string;
  city: string;
  state: string;
  country: string;
  postalCode: string;
  isDefault: boolean;
}

interface ShippingMethod {
  id: string;
  name: string;
  description: string;
  price: number;
  estimatedDays: string;
}

const SHIPPING_METHODS: ShippingMethod[] = [
  {
    id: 'standard',
    name: 'Standard Shipping',
    description: 'Delivered in 5-7 business days',
    price: 9.99,
    estimatedDays: '5-7'
  },
  {
    id: 'express',
    name: 'Express Shipping',
    description: 'Delivered in 2-3 business days',
    price: 19.99,
    estimatedDays: '2-3'
  },
  {
    id: 'overnight',
    name: 'Overnight Shipping',
    description: 'Delivered next business day',
    price: 39.99,
    estimatedDays: '1'
  }
];

const CheckoutWithStripe: React.FC = () => {
  const navigate = useNavigate();
  const { cart, clearCart } = useCart();
  // const { user } = useAuth(); // Will be used for user validation later
  
  const [isLoading, setIsLoading] = useState(false);
  const [addresses, setAddresses] = useState<Address[]>([]);
  const [selectedAddressId, setSelectedAddressId] = useState<number | null>(null);
  const [selectedShipping, setSelectedShipping] = useState<ShippingMethod>(SHIPPING_METHODS[0]);
  const [showAddressForm, setShowAddressForm] = useState(false);
  const [step, setStep] = useState(1);
  const [orderId, setOrderId] = useState<number | null>(null);
  const [clientSecret, setClientSecret] = useState<string | null>(null);
  
  const [newAddress, setNewAddress] = useState({
    name: '',
    phone: '',
    street: '',
    city: '',
    state: '',
    country: '',
    postalCode: '',
    isDefault: false
  });

  const subtotal = cart?.items.reduce((sum, item) => sum + (item.price * item.quantity), 0) || 0;
  const shippingCost = selectedShipping.price;
  const tax = subtotal * 0.08; // 8% tax
  const total = subtotal + shippingCost + tax;

  useEffect(() => {
    if (!cart || cart.items.length === 0) {
      navigate('/cart');
      return;
    }
    fetchAddresses();
  }, [cart, navigate]);

  const fetchAddresses = async () => {
    try {
      const response = await api.get('/addresses');
      setAddresses(response.data);
      // Auto-select default address
      const defaultAddress = response.data.find((addr: Address) => addr.isDefault);
      if (defaultAddress) {
        setSelectedAddressId(defaultAddress.id);
      }
    } catch (error) {
      console.error('Failed to fetch addresses:', error);
    }
  };

  const handleAddAddress = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    try {
      const response = await api.post('/addresses', newAddress);
      setAddresses([...addresses, response.data]);
      setSelectedAddressId(response.data.id);
      setShowAddressForm(false);
      setNewAddress({
        name: '',
        phone: '',
        street: '',
        city: '',
        state: '',
        country: '',
        postalCode: '',
        isDefault: false
      });
      toast.success('Address added successfully');
    } catch (error) {
      toast.error('Failed to add address');
    } finally {
      setIsLoading(false);
    }
  };

  const createOrder = async () => {
    if (!selectedAddressId) {
      toast.error('Please select a delivery address');
      return;
    }

    setIsLoading(true);
    try {
      // Create order
      const orderResponse = await api.post('/orders', {
        shippingAddressId: selectedAddressId,
        shippingMethod: selectedShipping.name,
        shippingCost: selectedShipping.price,
        notes: ''
      });

      const newOrderId = orderResponse.data.id;
      setOrderId(newOrderId);

      // Create payment intent
      const paymentResponse = await paymentService.createPaymentIntent(newOrderId);
      setClientSecret(paymentResponse.clientSecret);
      
      // Move to payment step
      setStep(3);
    } catch (error) {
      console.error('Failed to create order:', error);
      toast.error('Failed to create order. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handlePaymentSuccess = async () => {
    try {
      // Clear cart
      await clearCart();
      
      // Navigate to order confirmation
      if (orderId) {
        navigate(`/order-confirmation/${orderId}`);
      }
    } catch (error) {
      console.error('Error handling payment success:', error);
      toast.error('Payment successful but encountered an error. Please contact support.');
    }
  };

  const handlePaymentError = (error: string) => {
    toast.error(error);
  };

  const steps = [
    { id: 1, name: 'Address', icon: MapPinIcon },
    { id: 2, name: 'Shipping', icon: TruckIcon },
    { id: 3, name: 'Payment', icon: CreditCardIcon }
  ];


  return (
    <div className="min-h-screen bg-gray-50 py-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 className="text-3xl font-extrabold text-gray-900 mb-8">Checkout</h1>

        {/* Progress Steps */}
        <nav aria-label="Progress" className="mb-8">
          <ol role="list" className="flex items-center">
            {steps.map((stepItem, stepIdx) => (
              <li key={stepItem.name} className={stepIdx !== steps.length - 1 ? 'flex-1' : ''}>
                <div className="flex items-center">
                  <div
                    className={`flex items-center justify-center w-10 h-10 rounded-full ${
                      stepItem.id < step
                        ? 'bg-indigo-600'
                        : stepItem.id === step
                        ? 'bg-indigo-600'
                        : 'bg-gray-200'
                    }`}
                  >
                    {stepItem.id < step ? (
                      <CheckCircleIcon className="w-6 h-6 text-white" />
                    ) : (
                      <stepItem.icon className={`w-6 h-6 ${
                        stepItem.id === step ? 'text-white' : 'text-gray-500'
                      }`} />
                    )}
                  </div>
                  <span className={`ml-4 text-sm font-medium ${
                    stepItem.id <= step ? 'text-indigo-600' : 'text-gray-500'
                  }`}>
                    {stepItem.name}
                  </span>
                  {stepIdx !== steps.length - 1 && (
                    <div className={`flex-1 h-0.5 mx-4 ${
                      stepItem.id < step ? 'bg-indigo-600' : 'bg-gray-200'
                    }`} />
                  )}
                </div>
              </li>
            ))}
          </ol>
        </nav>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-2">
            {/* Step 1: Address Selection */}
            {step === 1 && (
              <div className="bg-white shadow rounded-lg p-6">
                <h2 className="text-lg font-medium text-gray-900 mb-4">Delivery Address</h2>
                
                {addresses.length === 0 && !showAddressForm && (
                  <div className="text-center py-8">
                    <MapPinIcon className="mx-auto h-12 w-12 text-gray-400" />
                    <p className="mt-2 text-sm text-gray-600">No addresses found</p>
                    <button
                      onClick={() => setShowAddressForm(true)}
                      className="mt-4 inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
                    >
                      <PlusIcon className="h-4 w-4 mr-2" />
                      Add Address
                    </button>
                  </div>
                )}

                {addresses.length > 0 && !showAddressForm && (
                  <div className="space-y-4">
                    {addresses.map((address) => (
                      <label
                        key={address.id}
                        className={`block p-4 border rounded-lg cursor-pointer transition ${
                          selectedAddressId === address.id
                            ? 'border-indigo-600 bg-indigo-50'
                            : 'border-gray-300 hover:border-gray-400'
                        }`}
                      >
                        <input
                          type="radio"
                          name="address"
                          value={address.id}
                          checked={selectedAddressId === address.id}
                          onChange={() => setSelectedAddressId(address.id)}
                          className="sr-only"
                        />
                        <div className="flex justify-between">
                          <div>
                            <p className="font-medium text-gray-900">{address.name}</p>
                            <p className="text-sm text-gray-600">{address.phone}</p>
                            <p className="text-sm text-gray-600">
                              {address.street}, {address.city}, {address.state} {address.postalCode}
                            </p>
                            <p className="text-sm text-gray-600">{address.country}</p>
                          </div>
                          {address.isDefault && (
                            <span className="text-xs bg-indigo-100 text-indigo-800 px-2 py-1 rounded-full h-fit">
                              Default
                            </span>
                          )}
                        </div>
                      </label>
                    ))}
                    <button
                      onClick={() => setShowAddressForm(true)}
                      className="w-full py-3 border-2 border-dashed border-gray-300 rounded-lg text-sm text-gray-600 hover:border-gray-400 hover:text-gray-900 transition"
                    >
                      <PlusIcon className="h-4 w-4 inline mr-2" />
                      Add New Address
                    </button>
                  </div>
                )}

                {showAddressForm && (
                  <form onSubmit={handleAddAddress} className="space-y-4">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700">Full Name</label>
                        <input
                          type="text"
                          required
                          value={newAddress.name}
                          onChange={(e) => setNewAddress({ ...newAddress, name: e.target.value })}
                          className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700">Phone Number</label>
                        <input
                          type="tel"
                          required
                          value={newAddress.phone}
                          onChange={(e) => setNewAddress({ ...newAddress, phone: e.target.value })}
                          className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                        />
                      </div>
                    </div>
                    {/* Add other address fields... */}
                    <div className="flex justify-between">
                      <button
                        type="button"
                        onClick={() => setShowAddressForm(false)}
                        className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
                      >
                        Cancel
                      </button>
                      <button
                        type="submit"
                        disabled={isLoading}
                        className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 border border-transparent rounded-md hover:bg-indigo-700 disabled:opacity-50"
                      >
                        {isLoading ? 'Adding...' : 'Add Address'}
                      </button>
                    </div>
                  </form>
                )}

                <div className="mt-6 flex justify-end">
                  <button
                    onClick={() => setStep(2)}
                    disabled={!selectedAddressId}
                    className="px-6 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    Continue to Shipping
                  </button>
                </div>
              </div>
            )}

            {/* Step 2: Shipping Method */}
            {step === 2 && (
              <div className="bg-white shadow rounded-lg p-6">
                <h2 className="text-lg font-medium text-gray-900 mb-4">Shipping Method</h2>
                <div className="space-y-4">
                  {SHIPPING_METHODS.map((method) => (
                    <label
                      key={method.id}
                      className={`block p-4 border rounded-lg cursor-pointer transition ${
                        selectedShipping.id === method.id
                          ? 'border-indigo-600 bg-indigo-50'
                          : 'border-gray-300 hover:border-gray-400'
                      }`}
                    >
                      <input
                        type="radio"
                        name="shipping"
                        value={method.id}
                        checked={selectedShipping.id === method.id}
                        onChange={() => setSelectedShipping(method)}
                        className="sr-only"
                      />
                      <div className="flex justify-between">
                        <div>
                          <p className="font-medium text-gray-900">{method.name}</p>
                          <p className="text-sm text-gray-600">{method.description}</p>
                        </div>
                        <p className="font-medium text-gray-900">${method.price.toFixed(2)}</p>
                      </div>
                    </label>
                  ))}
                </div>
                <div className="mt-6 flex justify-between">
                  <button
                    onClick={() => setStep(1)}
                    className="px-6 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300"
                  >
                    Back
                  </button>
                  <button
                    onClick={createOrder}
                    disabled={isLoading}
                    className="px-6 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 disabled:opacity-50"
                  >
                    {isLoading ? 'Processing...' : 'Continue to Payment'}
                  </button>
                </div>
              </div>
            )}

            {/* Step 3: Payment */}
            {step === 3 && clientSecret && (
              <div className="bg-white shadow rounded-lg p-6">
                <h2 className="text-lg font-medium text-gray-900 mb-4">Payment</h2>
                <Elements stripe={stripePromise} options={{ clientSecret }}>
                  <StripePaymentForm
                    clientSecret={clientSecret}
                    onSuccess={handlePaymentSuccess}
                    onError={handlePaymentError}
                  />
                </Elements>
                <div className="mt-6">
                  <button
                    onClick={() => setStep(2)}
                    className="px-6 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300"
                  >
                    Back
                  </button>
                </div>
              </div>
            )}
          </div>

          {/* Order Summary */}
          <div className="lg:col-span-1">
            <div className="bg-white shadow rounded-lg p-6 sticky top-4">
              <h2 className="text-lg font-medium text-gray-900 mb-4">Order Summary</h2>
              
              <div className="space-y-4 mb-6">
                {cart?.items.map((item) => (
                  <div key={item.id} className="flex justify-between text-sm">
                    <div className="flex-1">
                      <p className="font-medium text-gray-900">{item.productName}</p>
                      <p className="text-gray-600">Qty: {item.quantity}</p>
                    </div>
                    <p className="font-medium text-gray-900">
                      ${(item.price * item.quantity).toFixed(2)}
                    </p>
                  </div>
                ))}
              </div>

              <div className="border-t pt-4 space-y-2">
                <div className="flex justify-between text-sm">
                  <p className="text-gray-600">Subtotal</p>
                  <p className="font-medium text-gray-900">${subtotal.toFixed(2)}</p>
                </div>
                <div className="flex justify-between text-sm">
                  <p className="text-gray-600">Shipping</p>
                  <p className="font-medium text-gray-900">${shippingCost.toFixed(2)}</p>
                </div>
                <div className="flex justify-between text-sm">
                  <p className="text-gray-600">Tax</p>
                  <p className="font-medium text-gray-900">${tax.toFixed(2)}</p>
                </div>
                <div className="border-t pt-2 flex justify-between">
                  <p className="font-medium text-gray-900">Total</p>
                  <p className="text-lg font-bold text-gray-900">${total.toFixed(2)}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CheckoutWithStripe;