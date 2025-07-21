// User types
export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: 'BUYER' | 'SELLER' | 'ADMIN';
  emailVerified: boolean;
  phoneNumber?: string;
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';
  createdAt: string;
}

// Product types
export interface Product {
  id: number;
  sellerId: number;
  sellerName?: string;
  categoryId: number;
  categoryName?: string;
  categorySlug?: string;
  name: string;
  slug: string;
  description: string;
  shortDescription?: string;
  sku: string;
  price: number;
  compareAtPrice?: number;
  quantity: number;
  trackQuantity: boolean;
  weight?: number;
  weightUnit: string;
  status: 'ACTIVE' | 'INACTIVE' | 'DRAFT';
  featured: boolean;
  createdAt: string;
  updatedAt: string;
  publishedAt?: string;
  images?: ProductImage[];
  tags?: string[];
  attributes?: ProductAttribute[];
  variants?: ProductVariant[];
  averageRating?: number;
  reviewCount?: number;
  viewCount?: number;
  inStock: boolean;
  discountPercentage?: number;
}

export interface ProductImage {
  id: number;
  productId: number;
  imageUrl: string;
  altText?: string;
  displayOrder: number;
  isPrimary: boolean;
  createdAt: string;
}

export interface ProductAttribute {
  id: number;
  productId: number;
  attributeName: string;
  attributeValue: string;
}

export interface ProductVariant {
  id: number;
  productId: number;
  variantName: string;
  variantValue: string;
  price?: number;
  quantity?: number;
  sku?: string;
  displayOrder: number;
}

export interface ProductReview {
  id: number;
  productId: number;
  userId: number;
  userName?: string;
  rating: number;
  title?: string;
  comment?: string;
  isVerifiedPurchase: boolean;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  createdAt: string;
  updatedAt: string;
}

// Category types
export interface Category {
  id: number;
  name: string;
  slug: string;
  description?: string;
  parentId?: number;
  imageUrl?: string;
  isActive: boolean;
  displayOrder: number;
  createdAt: string;
  updatedAt: string;
}

// Search and filter types
export interface ProductSearchParams {
  keyword?: string;
  categoryId?: number;
  sellerId?: number;
  minPrice?: number;
  maxPrice?: number;
  featured?: boolean;
  tags?: string[];
  sortBy?: 'name' | 'price' | 'created_at' | 'rating';
  limit?: number;
  offset?: number;
}

// Cart types
export interface CartItem {
  id: string;
  productId: number;
  product: Product;
  quantity: number;
  selectedVariants?: { [key: string]: string };
  addedAt: string;
}

export interface Cart {
  items: CartItem[];
  totalItems: number;
  totalPrice: number;
}

// Order types
export interface Order {
  id: number;
  userId: number;
  status: 'PENDING' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
  totalAmount: number;
  shippingAddress: Address;
  items: OrderItem[];
  createdAt: string;
  updatedAt: string;
}

export interface OrderItem {
  id: number;
  orderId: number;
  productId: number;
  product: Product;
  quantity: number;
  price: number;
  totalPrice: number;
}

// Address types
export interface Address {
  id: number;
  userId: number;
  type: 'SHIPPING' | 'BILLING';
  firstName: string;
  lastName: string;
  company?: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  phone?: string;
  isDefault: boolean;
  createdAt: string;
  updatedAt: string;
}

// API response types
export interface ApiResponse<T> {
  data: T;
  success: boolean;
  message?: string;
}

// Form types
export interface CreateProductRequest {
  name: string;
  description: string;
  shortDescription?: string;
  categoryId: number;
  price: number;
  compareAtPrice?: number;
  quantity: number;
  trackQuantity: boolean;
  weight?: number;
  weightUnit: string;
  status: 'ACTIVE' | 'INACTIVE' | 'DRAFT';
  featured: boolean;
  tags?: string[];
  attributes?: { [key: string]: string };
  images?: { imageUrl: string; altText?: string }[];
  variants?: {
    variantName: string;
    variantValue: string;
    price?: number;
    quantity?: number;
    sku?: string;
  }[];
}

export interface CreateReviewRequest {
  productId: number;
  rating: number;
  title?: string;
  comment?: string;
}