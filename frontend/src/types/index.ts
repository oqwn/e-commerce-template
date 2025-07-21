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

// Order types
export interface Order {
  id: number;
  orderNumber: string;
  status: 'PENDING' | 'CONFIRMED' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED' | 'REFUNDED';
  paymentStatus: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED' | 'REFUNDED';
  totalAmount: number;
  subtotalAmount: number;
  taxAmount: number;
  shippingAmount: number;
  discountAmount?: number;
  shippingAddress: Address;
  billingAddress?: Address;
  paymentMethod: string;
  notes?: string;
  estimatedDeliveryDate?: string;
  deliveredAt?: string;
  cancelledAt?: string;
  createdAt: string;
  updatedAt: string;
  items: OrderItem[];
  payments?: Payment[];
  totalItems: number;
}

export interface OrderItem {
  id: number;
  orderId: number;
  productId: number;
  productName: string;
  productSlug: string;
  productImageUrl?: string;
  productSku: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  variantOptions?: string;
}

export interface Payment {
  id: number;
  orderId: number;
  amount: number;
  paymentMethod: string;
  paymentStatus: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED' | 'REFUNDED';
  transactionId?: string;
  createdAt: string;
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

// Store types
export interface Store {
  id: number;
  sellerId: number;
  storeName: string;
  storeSlug: string;
  description: string;
  logoUrl?: string;
  bannerUrl?: string;
  businessName: string;
  businessRegistrationNumber?: string;
  taxId?: string;
  contactEmail: string;
  contactPhone: string;
  supportEmail?: string;
  supportPhone?: string;
  street: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  currency: string;
  timezone: string;
  isActive: boolean;
  isVerified: boolean;
  verificationStatus: 'PENDING' | 'IN_REVIEW' | 'APPROVED' | 'REJECTED' | 'SUSPENDED';
  verificationDate?: string;
  returnPolicy?: string;
  shippingPolicy?: string;
  privacyPolicy?: string;
  termsAndConditions?: string;
  websiteUrl?: string;
  facebookUrl?: string;
  instagramUrl?: string;
  twitterUrl?: string;
  youtubeUrl?: string;
  rating: number;
  totalReviews: number;
  totalProducts: number;
  totalSales: number;
  createdAt: string;
  updatedAt: string;
}

export interface StoreCustomization {
  id: number;
  storeId: number;
  themeName: string;
  primaryColor: string;
  secondaryColor: string;
  accentColor: string;
  backgroundColor: string;
  textColor: string;
  layoutType: 'GRID' | 'LIST' | 'MASONRY';
  productsPerPage: number;
  showBanner: boolean;
  showFeaturedProducts: boolean;
  showCategories: boolean;
  customCss?: string;
  metaTitle?: string;
  metaDescription?: string;
  metaKeywords?: string;
  createdAt: string;
  updatedAt: string;
}

export interface StoreOperatingHours {
  id: number;
  storeId: number;
  dayOfWeek: number; // 0=Sunday, 1=Monday, ..., 6=Saturday
  openTime?: string;
  closeTime?: string;
  isClosed: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface StoreAnalytics {
  id: number;
  storeId: number;
  date: string;
  totalVisits: number;
  uniqueVisitors: number;
  pageViews: number;
  bounceRate: number;
  avgSessionDuration: number;
  totalOrders: number;
  totalRevenue: number;
  avgOrderValue: number;
  conversionRate: number;
  productsViewed: number;
  productsAddedToCart: number;
  productsPurchased: number;
  createdAt: string;
  updatedAt: string;
}