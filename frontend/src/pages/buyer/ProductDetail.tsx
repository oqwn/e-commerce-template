import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { Heart, Share2, Star, Shield, Truck, RefreshCw, Minus, Plus } from 'lucide-react'
import { api } from '@/services/api'
import LoadingSpinner from '@/components/common/LoadingSpinner'
import AnimatedAddToCartButton from '@/components/ui/AnimatedAddToCartButton'
import { toast } from 'react-toastify'

interface ProductImage {
  id: number
  imageUrl: string
  altText: string
  displayOrder: number
  isPrimary: boolean
}

interface Product {
  id: number
  name: string
  slug: string
  description: string
  price: number
  compareAtPrice?: number
  quantity: number
  category: {
    id: number
    name: string
  }
  store: {
    id: number
    name: string
    slug: string
  }
  images: ProductImage[]
  averageRating: number
  reviewCount: number
  tags: string[]
  attributes: Record<string, string>
  inStock: boolean
  discountPercentage?: number
}

interface ProductReview {
  id: number
  user: {
    id: number
    firstName: string
    lastName: string
  }
  rating: number
  comment: string
  verifiedPurchase: boolean
  createdAt: string
}

export default function ProductDetail() {
  const { slug } = useParams<{ slug: string }>()
  const navigate = useNavigate()
  const [product, setProduct] = useState<Product | null>(null)
  const [reviews, setReviews] = useState<ProductReview[]>([])
  const [loading, setLoading] = useState(true)
  const [selectedImage, setSelectedImage] = useState(0)
  const [quantity, setQuantity] = useState(1)

  useEffect(() => {
    if (slug) {
      fetchProductDetails()
    }
  }, [slug])

  const fetchProductDetails = async () => {
    try {
      setLoading(true)
      const response = await api.get(`/products/slug/${slug}`)
      setProduct(response.data.data)
      
      // Fetch reviews
      try {
        const reviewsResponse = await api.get(`/products/${response.data.data.id}/reviews`)
        setReviews(reviewsResponse.data.data || [])
      } catch (error) {
        console.error('Error fetching reviews:', error)
      }
    } catch (error) {
      console.error('Error fetching product:', error)
      toast.error('Failed to load product details')
      navigate('/buyer/products')
    } finally {
      setLoading(false)
    }
  }


  const handleQuantityChange = (delta: number) => {
    const newQuantity = quantity + delta
    if (newQuantity >= 1 && newQuantity <= (product?.quantity || 1)) {
      setQuantity(newQuantity)
    }
  }

  const renderStars = (rating: number) => {
    return Array.from({ length: 5 }, (_, i) => (
      <Star
        key={i}
        className={`w-4 h-4 ${
          i < Math.floor(rating)
            ? 'text-yellow-400 fill-current'
            : 'text-gray-300'
        }`}
      />
    ))
  }

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <LoadingSpinner />
      </div>
    )
  }

  if (!product) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-semibold text-gray-900 mb-2">Product not found</h2>
          <p className="text-gray-600 mb-4">The product you're looking for doesn't exist.</p>
          <button
            onClick={() => navigate('/buyer/products')}
            className="bg-indigo-600 text-white px-6 py-2 rounded-md hover:bg-indigo-700"
          >
            Back to Products
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Breadcrumb */}
      <nav className="flex mb-8" aria-label="Breadcrumb">
        <ol className="flex items-center space-x-4">
          <li>
            <a href="/buyer/products" className="text-gray-400 hover:text-gray-500">
              Products
            </a>
          </li>
          <li>
            <div className="flex items-center">
              <svg className="flex-shrink-0 h-5 w-5 text-gray-300" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clipRule="evenodd" />
              </svg>
              <span className="ml-4 text-gray-400">{product.category.name}</span>
            </div>
          </li>
          <li>
            <div className="flex items-center">
              <svg className="flex-shrink-0 h-5 w-5 text-gray-300" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clipRule="evenodd" />
              </svg>
              <span className="ml-4 text-gray-500 font-medium">{product.name}</span>
            </div>
          </li>
        </ol>
      </nav>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Image Gallery */}
        <div className="space-y-4">
          <div className="aspect-w-1 aspect-h-1 bg-gray-200 rounded-lg overflow-hidden">
            <img
              src={product.images[selectedImage]?.imageUrl || '/placeholder.jpg'}
              alt={product.images[selectedImage]?.altText || product.name}
              className="w-full h-full object-center object-cover"
            />
          </div>
          
          {/* Thumbnail Images */}
          {product.images.length > 1 && (
            <div className="grid grid-cols-4 gap-4">
              {product.images.map((image, index) => (
                <button
                  key={image.id}
                  onClick={() => setSelectedImage(index)}
                  className={`aspect-w-1 aspect-h-1 bg-gray-200 rounded-md overflow-hidden ${
                    selectedImage === index ? 'ring-2 ring-indigo-500' : ''
                  }`}
                >
                  <img
                    src={image.imageUrl}
                    alt={image.altText}
                    className="w-full h-full object-center object-cover"
                  />
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Product Info */}
        <div className="space-y-6">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">{product.name}</h1>
            <p className="mt-2 text-sm text-gray-500">
              Sold by <a href={`/stores/${product.store.slug}`} className="text-indigo-600 hover:text-indigo-500">{product.store.name}</a>
            </p>
          </div>

          {/* Rating */}
          <div className="flex items-center space-x-2">
            <div className="flex">{renderStars(product.averageRating)}</div>
            <span className="text-sm text-gray-600">
              {product.averageRating.toFixed(1)} ({product.reviewCount} reviews)
            </span>
          </div>

          {/* Price */}
          <div className="flex items-baseline space-x-3">
            <span className="text-3xl font-bold text-gray-900">${product.price.toFixed(2)}</span>
            {product.compareAtPrice && (
              <>
                <span className="text-xl text-gray-500 line-through">${product.compareAtPrice.toFixed(2)}</span>
                {product.discountPercentage && (
                  <span className="text-sm font-medium text-red-600">
                    {product.discountPercentage}% OFF
                  </span>
                )}
              </>
            )}
          </div>

          {/* Stock Status */}
          <div>
            {product.inStock ? (
              <p className="text-sm text-green-600">✓ In Stock ({product.quantity} available)</p>
            ) : (
              <p className="text-sm text-red-600">Out of Stock</p>
            )}
          </div>

          {/* Quantity Selector */}
          <div className="space-y-4">
            <div className="flex items-center space-x-4">
              <label className="text-sm font-medium text-gray-700">Quantity:</label>
              <div className="flex items-center border border-gray-300 rounded-md">
                <button
                  onClick={() => handleQuantityChange(-1)}
                  disabled={quantity <= 1}
                  className="p-2 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <Minus className="w-4 h-4" />
                </button>
                <span className="px-4 py-2 font-medium">{quantity}</span>
                <button
                  onClick={() => handleQuantityChange(1)}
                  disabled={quantity >= product.quantity}
                  className="p-2 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <Plus className="w-4 h-4" />
                </button>
              </div>
            </div>

            {/* Action Buttons */}
            <div className="flex space-x-4">
              <AnimatedAddToCartButton
                productId={product.id}
                productName={product.name}
                productImageUrl={product.images[0]?.imageUrl}
                disabled={!product.inStock}
                className="flex-1"
                size="lg"
              />
              <button className="p-3 border border-gray-300 rounded-md hover:bg-gray-50">
                <Heart className="w-5 h-5 text-gray-600" />
              </button>
              <button className="p-3 border border-gray-300 rounded-md hover:bg-gray-50">
                <Share2 className="w-5 h-5 text-gray-600" />
              </button>
            </div>
          </div>

          {/* Features */}
          <div className="border-t border-gray-200 pt-6 space-y-4">
            <div className="flex items-center space-x-3">
              <Truck className="w-5 h-5 text-gray-400" />
              <span className="text-sm text-gray-600">Free shipping on orders over $50</span>
            </div>
            <div className="flex items-center space-x-3">
              <RefreshCw className="w-5 h-5 text-gray-400" />
              <span className="text-sm text-gray-600">30-day return policy</span>
            </div>
            <div className="flex items-center space-x-3">
              <Shield className="w-5 h-5 text-gray-400" />
              <span className="text-sm text-gray-600">Secure payment</span>
            </div>
          </div>

          {/* Product Attributes */}
          {Object.keys(product.attributes).length > 0 && (
            <div className="border-t border-gray-200 pt-6">
              <h3 className="text-sm font-medium text-gray-900 mb-4">Product Details</h3>
              <dl className="space-y-2">
                {Object.entries(product.attributes).map(([key, value]) => (
                  <div key={key} className="flex justify-between text-sm">
                    <dt className="text-gray-500">{key}:</dt>
                    <dd className="text-gray-900 font-medium">{value}</dd>
                  </div>
                ))}
              </dl>
            </div>
          )}

          {/* Tags */}
          {product.tags.length > 0 && (
            <div className="border-t border-gray-200 pt-6">
              <h3 className="text-sm font-medium text-gray-900 mb-4">Tags</h3>
              <div className="flex flex-wrap gap-2">
                {product.tags.map((tag, index) => (
                  <span
                    key={index}
                    className="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-gray-100 text-gray-800"
                  >
                    {tag}
                  </span>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Description */}
      <div className="mt-12 border-t border-gray-200 pt-8">
        <h2 className="text-2xl font-bold text-gray-900 mb-4">Description</h2>
        <div className="prose max-w-none text-gray-600">
          {product.description.split('\n').map((paragraph, index) => (
            <p key={index} className="mb-4">{paragraph}</p>
          ))}
        </div>
      </div>

      {/* Reviews Section */}
      <div className="mt-12 border-t border-gray-200 pt-8">
        <h2 className="text-2xl font-bold text-gray-900 mb-6">Customer Reviews</h2>
        
        {reviews.length > 0 ? (
          <div className="space-y-6">
            {reviews.map((review) => (
              <div key={review.id} className="border-b border-gray-200 pb-6">
                <div className="flex items-center justify-between mb-2">
                  <div className="flex items-center space-x-3">
                    <span className="font-medium text-gray-900">
                      {review.user.firstName} {review.user.lastName}
                    </span>
                    {review.verifiedPurchase && (
                      <span className="text-xs bg-green-100 text-green-800 px-2 py-1 rounded">
                        Verified Purchase
                      </span>
                    )}
                  </div>
                  <span className="text-sm text-gray-500">
                    {new Date(review.createdAt).toLocaleDateString()}
                  </span>
                </div>
                <div className="flex items-center mb-2">
                  {renderStars(review.rating)}
                </div>
                <p className="text-gray-600">{review.comment}</p>
              </div>
            ))}
          </div>
        ) : (
          <p className="text-gray-500">No reviews yet. Be the first to review this product!</p>
        )}
      </div>
    </div>
  )
}