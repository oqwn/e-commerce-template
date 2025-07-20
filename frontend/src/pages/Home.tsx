import React from 'react';

const Home: React.FC = () => {
  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">
        Welcome to E-Commerce Store
      </h1>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white p-6 rounded-lg shadow-md">
          <h2 className="text-xl font-semibold mb-4">Products</h2>
          <p className="text-gray-600">Browse our collection of products</p>
        </div>
        <div className="bg-white p-6 rounded-lg shadow-md">
          <h2 className="text-xl font-semibold mb-4">Categories</h2>
          <p className="text-gray-600">Explore product categories</p>
        </div>
        <div className="bg-white p-6 rounded-lg shadow-md">
          <h2 className="text-xl font-semibold mb-4">Account</h2>
          <p className="text-gray-600">Manage your account and orders</p>
        </div>
      </div>
    </div>
  );
};

export default Home;