import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '@/services/api';
import { Store, StoreCustomization } from '@/types';
import {
  BuildingStorefrontIcon,
  CameraIcon,
  DocumentTextIcon,
  GlobeAltIcon,
  PaintBrushIcon,
  ClockIcon,
  CheckCircleIcon,
  ExclamationCircleIcon
} from '@heroicons/react/24/outline';

interface StoreWithDetails extends Store {
  customization?: StoreCustomization;
  operatingHours?: any[];
}

const StoreManagement: React.FC = () => {
  const navigate = useNavigate();
  const [store, setStore] = useState<StoreWithDetails | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('general');
  const [isSaving, setIsSaving] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });

  useEffect(() => {
    fetchStore();
  }, []);

  const fetchStore = async () => {
    try {
      const response = await api.get('/stores/my-store');
      if (response.data) {
        setStore(response.data);
      } else {
        navigate('/seller/store/register');
      }
    } catch (error) {
      console.error('Error fetching store:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleImageUpload = async (type: 'logo' | 'banner', file: File) => {
    if (!store) return;

    const formData = new FormData();
    formData.append('file', file);

    try {
      setIsSaving(true);
      const data = await api.upload(`/stores/${store.id}/${type}`, formData);
      
      setStore(prev => prev ? {
        ...prev,
        [type === 'logo' ? 'logoUrl' : 'bannerUrl']: data.data
      } : null);
      
      setMessage({ type: 'success', text: `${type} uploaded successfully!` });
    } catch (error) {
      setMessage({ type: 'error', text: `Failed to upload ${type}` });
    } finally {
      setIsSaving(false);
    }
  };

  const handleGeneralInfoUpdate = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!store) return;

    const formData = new FormData(e.currentTarget);
    const data = Object.fromEntries(formData.entries());

    try {
      setIsSaving(true);
      const response = await api.put(`/stores/${store.id}`, data);
      setStore(response.data);
      setMessage({ type: 'success', text: 'Store information updated successfully!' });
    } catch (error) {
      setMessage({ type: 'error', text: 'Failed to update store information' });
    } finally {
      setIsSaving(false);
    }
  };

  const handleCustomizationUpdate = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!store) return;

    const formData = new FormData(e.currentTarget);
    const data = Object.fromEntries(formData.entries());

    try {
      setIsSaving(true);
      const response = await api.put(`/stores/${store.id}/customization`, data);
      setStore(prev => prev ? { ...prev, customization: response.data } : null);
      setMessage({ type: 'success', text: 'Customization updated successfully!' });
    } catch (error) {
      setMessage({ type: 'error', text: 'Failed to update customization' });
    } finally {
      setIsSaving(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600"></div>
      </div>
    );
  }

  if (!store) {
    return null;
  }

  const tabs = [
    { id: 'general', name: 'General Info', icon: BuildingStorefrontIcon },
    { id: 'media', name: 'Media', icon: CameraIcon },
    { id: 'customization', name: 'Customization', icon: PaintBrushIcon },
    { id: 'policies', name: 'Policies', icon: DocumentTextIcon },
    { id: 'social', name: 'Social Links', icon: GlobeAltIcon },
    { id: 'hours', name: 'Operating Hours', icon: ClockIcon },
  ];

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="bg-white shadow-lg rounded-lg">
        {/* Header */}
        <div className="bg-gradient-to-r from-green-600 to-green-700 px-6 py-4 rounded-t-lg">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-4">
              <BuildingStorefrontIcon className="h-8 w-8 text-white" />
              <div>
                <h1 className="text-2xl font-bold text-white">{store.storeName}</h1>
                <p className="text-green-100 text-sm">Manage your store settings</p>
              </div>
            </div>
            <div className="flex items-center space-x-2">
              {store.isVerified ? (
                <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                  <CheckCircleIcon className="h-4 w-4 mr-1" />
                  Verified
                </span>
              ) : (
                <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-yellow-100 text-yellow-800">
                  <ExclamationCircleIcon className="h-4 w-4 mr-1" />
                  Pending Verification
                </span>
              )}
              {store.isActive ? (
                <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                  Active
                </span>
              ) : (
                <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-red-100 text-red-800">
                  Inactive
                </span>
              )}
            </div>
          </div>
        </div>

        {/* Tabs */}
        <div className="border-b border-gray-200">
          <nav className="-mb-px flex space-x-8 px-6" aria-label="Tabs">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`
                  flex items-center py-4 px-1 border-b-2 font-medium text-sm
                  ${activeTab === tab.id
                    ? 'border-green-500 text-green-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  }
                `}
              >
                <tab.icon className="h-5 w-5 mr-2" />
                {tab.name}
              </button>
            ))}
          </nav>
        </div>

        {/* Message */}
        {message.text && (
          <div className={`mx-6 mt-4 p-4 rounded-md ${
            message.type === 'success' ? 'bg-green-50 text-green-800' : 'bg-red-50 text-red-800'
          }`}>
            {message.text}
          </div>
        )}

        {/* Content */}
        <div className="p-6">
          {/* General Info Tab */}
          {activeTab === 'general' && (
            <form onSubmit={handleGeneralInfoUpdate} className="space-y-6">
              <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
                <div>
                  <label htmlFor="storeName" className="block text-sm font-medium text-gray-700">
                    Store Name
                  </label>
                  <input
                    type="text"
                    name="storeName"
                    id="storeName"
                    defaultValue={store.storeName}
                    className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-green-500 focus:border-green-500 sm:text-sm"
                  />
                </div>

                <div>
                  <label htmlFor="businessName" className="block text-sm font-medium text-gray-700">
                    Business Name
                  </label>
                  <input
                    type="text"
                    name="businessName"
                    id="businessName"
                    defaultValue={store.businessName}
                    className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-green-500 focus:border-green-500 sm:text-sm"
                  />
                </div>

                <div className="sm:col-span-2">
                  <label htmlFor="description" className="block text-sm font-medium text-gray-700">
                    Description
                  </label>
                  <textarea
                    name="description"
                    id="description"
                    rows={4}
                    defaultValue={store.description}
                    className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-green-500 focus:border-green-500 sm:text-sm"
                  />
                </div>

                <div>
                  <label htmlFor="contactEmail" className="block text-sm font-medium text-gray-700">
                    Contact Email
                  </label>
                  <input
                    type="email"
                    name="contactEmail"
                    id="contactEmail"
                    defaultValue={store.contactEmail}
                    className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-green-500 focus:border-green-500 sm:text-sm"
                  />
                </div>

                <div>
                  <label htmlFor="contactPhone" className="block text-sm font-medium text-gray-700">
                    Contact Phone
                  </label>
                  <input
                    type="tel"
                    name="contactPhone"
                    id="contactPhone"
                    defaultValue={store.contactPhone}
                    className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-green-500 focus:border-green-500 sm:text-sm"
                  />
                </div>
              </div>

              <div className="flex justify-end">
                <button
                  type="submit"
                  disabled={isSaving}
                  className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50"
                >
                  {isSaving ? 'Saving...' : 'Save Changes'}
                </button>
              </div>
            </form>
          )}

          {/* Media Tab */}
          {activeTab === 'media' && (
            <div className="space-y-8">
              <div>
                <h3 className="text-lg font-medium text-gray-900 mb-4">Store Logo</h3>
                <div className="flex items-center space-x-6">
                  {store.logoUrl ? (
                    <img
                      src={store.logoUrl}
                      alt="Store logo"
                      className="h-32 w-32 object-cover rounded-lg shadow-md"
                    />
                  ) : (
                    <div className="h-32 w-32 bg-gray-200 rounded-lg flex items-center justify-center">
                      <CameraIcon className="h-12 w-12 text-gray-400" />
                    </div>
                  )}
                  <div>
                    <label className="cursor-pointer inline-flex items-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500">
                      <CameraIcon className="h-5 w-5 mr-2 text-gray-400" />
                      Upload Logo
                      <input
                        type="file"
                        className="sr-only"
                        accept="image/*"
                        onChange={(e) => {
                          const file = e.target.files?.[0];
                          if (file) handleImageUpload('logo', file);
                        }}
                      />
                    </label>
                    <p className="mt-2 text-sm text-gray-500">
                      Recommended: 400x400px, PNG or JPG
                    </p>
                  </div>
                </div>
              </div>

              <div>
                <h3 className="text-lg font-medium text-gray-900 mb-4">Store Banner</h3>
                <div className="space-y-4">
                  {store.bannerUrl ? (
                    <img
                      src={store.bannerUrl}
                      alt="Store banner"
                      className="w-full h-48 object-cover rounded-lg shadow-md"
                    />
                  ) : (
                    <div className="w-full h-48 bg-gray-200 rounded-lg flex items-center justify-center">
                      <CameraIcon className="h-12 w-12 text-gray-400" />
                    </div>
                  )}
                  <div>
                    <label className="cursor-pointer inline-flex items-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500">
                      <CameraIcon className="h-5 w-5 mr-2 text-gray-400" />
                      Upload Banner
                      <input
                        type="file"
                        className="sr-only"
                        accept="image/*"
                        onChange={(e) => {
                          const file = e.target.files?.[0];
                          if (file) handleImageUpload('banner', file);
                        }}
                      />
                    </label>
                    <p className="mt-2 text-sm text-gray-500">
                      Recommended: 1200x300px, PNG or JPG
                    </p>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Customization Tab */}
          {activeTab === 'customization' && store.customization && (
            <form onSubmit={handleCustomizationUpdate} className="space-y-6">
              <div>
                <h3 className="text-lg font-medium text-gray-900 mb-4">Theme Colors</h3>
                <div className="grid grid-cols-2 gap-4 sm:grid-cols-3">
                  <div>
                    <label htmlFor="primaryColor" className="block text-sm font-medium text-gray-700">
                      Primary Color
                    </label>
                    <div className="mt-1 flex items-center">
                      <input
                        type="color"
                        name="primaryColor"
                        id="primaryColor"
                        defaultValue={store.customization.primaryColor}
                        className="h-10 w-20 border border-gray-300 rounded cursor-pointer"
                      />
                      <input
                        type="text"
                        value={store.customization.primaryColor}
                        readOnly
                        className="ml-2 block w-full border-gray-300 rounded-md shadow-sm text-sm"
                      />
                    </div>
                  </div>

                  <div>
                    <label htmlFor="secondaryColor" className="block text-sm font-medium text-gray-700">
                      Secondary Color
                    </label>
                    <div className="mt-1 flex items-center">
                      <input
                        type="color"
                        name="secondaryColor"
                        id="secondaryColor"
                        defaultValue={store.customization.secondaryColor}
                        className="h-10 w-20 border border-gray-300 rounded cursor-pointer"
                      />
                      <input
                        type="text"
                        value={store.customization.secondaryColor}
                        readOnly
                        className="ml-2 block w-full border-gray-300 rounded-md shadow-sm text-sm"
                      />
                    </div>
                  </div>

                  <div>
                    <label htmlFor="accentColor" className="block text-sm font-medium text-gray-700">
                      Accent Color
                    </label>
                    <div className="mt-1 flex items-center">
                      <input
                        type="color"
                        name="accentColor"
                        id="accentColor"
                        defaultValue={store.customization.accentColor}
                        className="h-10 w-20 border border-gray-300 rounded cursor-pointer"
                      />
                      <input
                        type="text"
                        value={store.customization.accentColor}
                        readOnly
                        className="ml-2 block w-full border-gray-300 rounded-md shadow-sm text-sm"
                      />
                    </div>
                  </div>
                </div>
              </div>

              <div>
                <h3 className="text-lg font-medium text-gray-900 mb-4">Layout Settings</h3>
                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                  <div>
                    <label htmlFor="layoutType" className="block text-sm font-medium text-gray-700">
                      Layout Type
                    </label>
                    <select
                      name="layoutType"
                      id="layoutType"
                      defaultValue={store.customization.layoutType}
                      className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-green-500 focus:border-green-500 sm:text-sm"
                    >
                      <option value="GRID">Grid</option>
                      <option value="LIST">List</option>
                      <option value="MASONRY">Masonry</option>
                    </select>
                  </div>

                  <div>
                    <label htmlFor="productsPerPage" className="block text-sm font-medium text-gray-700">
                      Products Per Page
                    </label>
                    <input
                      type="number"
                      name="productsPerPage"
                      id="productsPerPage"
                      min="12"
                      max="60"
                      step="12"
                      defaultValue={store.customization.productsPerPage}
                      className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-green-500 focus:border-green-500 sm:text-sm"
                    />
                  </div>
                </div>
              </div>

              <div className="flex justify-end">
                <button
                  type="submit"
                  disabled={isSaving}
                  className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50"
                >
                  {isSaving ? 'Saving...' : 'Save Customization'}
                </button>
              </div>
            </form>
          )}
        </div>
      </div>
    </div>
  );
};

export default StoreManagement;