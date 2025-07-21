# E-Commerce Platform Architecture

## Overview

This document outlines the architecture for an open-source, modular e-commerce platform built with Spring Boot (backend) and React (frontend). The platform emphasizes modularity, extensibility, and community-driven development.

## Design Principles

- **Modular Architecture**: Each feature module can be developed and deployed independently
- **Plugin-First Approach**: Core functionality can be extended through plugins
- **Multi-Tenancy Ready**: Supports SaaS deployment for multiple clients
- **API-First Design**: RESTful APIs with GraphQL support for flexible data queries
- **Internationalization**: Built-in support for multiple languages and currencies
- **Cloud-Native**: Docker and Kubernetes ready for elastic scaling

## System Architecture

### High-Level Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Web Client    │    │  Mobile App     │    │  Admin Panel    │
│   (React SPA)   │    │   (React)       │    │   (React)       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │   API Gateway   │
                    │  (Rate Limiting,│
                    │   Auth, CORS)   │
                    └─────────────────┘
                                 │
         ┌───────────────────────┼───────────────────────┐
         │                       │                       │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   User Service  │    │ Product Service │    │ Store Service   │
│   (Spring Boot) │    │ (Spring Boot)   │    │ (Spring Boot)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │   Data Layer    │
                    │ (PostgreSQL +   │
                    │     Redis)      │
                    └─────────────────┘
```

### Security Configuration

The platform uses Spring Security with the following endpoint authorization rules:

- **Public endpoints**: `/auth/**`, `/health`, `/actuator/**`
- **Store endpoints**: 
  - GET `/stores/**` - Public access for browsing stores
  - Other `/stores/**` - Authenticated access for store management
- **Admin endpoints**: `/admin/**` - Admin role required
- **Seller endpoints**: `/seller/**` - Seller or Admin role required  
- **User endpoints**: `/users/**` - Authenticated users only

### Core Modules

#### 1. User Management Module ✅ **IMPLEMENTED**
**Package**: `com.ecommerce`
- ✅ User registration, authentication, and authorization (JWT-based)
- ✅ Profile management and security settings (UserController, AddressController)
- ✅ Multi-role support (Buyer, Seller, Admin) with @PreAuthorize
- ✅ Email verification with token-based validation
- ✅ Password reset functionality with secure tokens
- ✅ Address management with multiple addresses per user
- 🚧 OAuth 2.0 integration for third-party login (UI ready, backend pending)

#### 2. Product Management Module
**Package**: `com.ecommerce.product`
- Product catalog and inventory management
- Category and attribute management
- Search and filtering capabilities
- Product reviews and ratings

#### 3. Order Management Module
**Package**: `com.ecommerce.order`
- Shopping cart functionality
- Order processing and fulfillment
- Payment integration
- Shipping and logistics tracking

#### 4. Store Management Module
**Package**: `com.ecommerce.store`
- Seller store creation and management
- Store analytics and performance metrics
- Store customization and branding

#### 5. Marketing Module
**Package**: `com.ecommerce.marketing`
- Coupon and discount management
- Campaign creation and management
- Social commerce features (group buying, live streaming)

#### 6. Analytics Module
**Package**: `com.ecommerce.analytics`
- User behavior tracking
- Sales and performance analytics
- Real-time dashboards and reporting

### Technology Stack

#### Backend ✅ **IMPLEMENTED**
- **Framework**: Spring Boot 3.4.5 ✅
- **Database**: H2 with PostgreSQL compatibility mode (production-ready migration system) ✅
- **Security**: Spring Security with JWT authentication ✅
- **ORM**: MyBatis for SQL mapping ✅
- **Migration**: Flyway for database versioning ✅
- **Validation**: Bean validation with custom DTOs ✅
- **Email**: Spring Mail for verification and password reset ✅
- **Monitoring**: Actuator endpoints with Prometheus metrics ✅
- **Testing**: Basic structure ready, JUnit 5 configured ✅

#### Frontend ✅ **IMPLEMENTED**
- **Framework**: React 18+ with TypeScript ✅
- **State Management**: Context API for authentication ✅
- **UI Components**: Tailwind CSS + custom components ✅
- **Build Tool**: Vite ✅
- **Authentication**: Complete auth flow with JWT handling ✅
- **Routing**: React Router with protected routes ✅
- **Code Quality**: ESLint + TypeScript strict mode ✅
- **Testing**: Vitest configured, basic structure ready ✅

#### Infrastructure
- **Containerization**: Docker
- **Orchestration**: Kubernetes
- **CI/CD**: GitHub Actions
- **API Documentation**: OpenAPI 3.0 (Swagger)

## Database Design

### Core Entities

#### Users ✅ **IMPLEMENTED**
```sql
-- Migration V1__Initial_schema.sql
users (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  phone VARCHAR(20),
  role VARCHAR(20) NOT NULL DEFAULT 'BUYER' CHECK (role IN ('BUYER', 'SELLER', 'ADMIN')),
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
  email_verified BOOLEAN NOT NULL DEFAULT FALSE,
  email_verification_token VARCHAR(255),
  email_verification_expiry TIMESTAMP,
  password_reset_token VARCHAR(255),
  password_reset_expiry TIMESTAMP,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)

addresses (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  type VARCHAR(20) NOT NULL DEFAULT 'HOME' CHECK (type IN ('HOME', 'WORK', 'OTHER')),
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  phone VARCHAR(20),
  street VARCHAR(255),
  city VARCHAR(100),
  state VARCHAR(100),
  postal_code VARCHAR(20),
  country VARCHAR(2) NOT NULL DEFAULT 'US',
  is_default BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)
```

#### Products
```sql
categories (
  id, name, slug, parent_id, description,
  image_url, sort_order, is_active
)

products (
  id, seller_id, category_id, name, slug,
  description, price, cost, stock_quantity,
  sku, status, created_at, updated_at
)

product_images (
  id, product_id, image_url, alt_text,
  sort_order, is_primary
)

product_reviews (
  id, product_id, user_id, rating, comment,
  images, verified_purchase, created_at
)
```

#### Orders
```sql
carts (
  id, user_id, session_id, expires_at,
  created_at, updated_at
)

cart_items (
  id, cart_id, product_id, quantity,
  price_at_time, created_at
)

orders (
  id, user_id, status, total_amount,
  shipping_address, billing_address,
  payment_method, created_at, updated_at
)

order_items (
  id, order_id, product_id, quantity,
  price_at_time, total_price
)
```

#### Stores
```sql
stores (
  id, seller_id, name, slug, description,
  logo_url, banner_url, status, created_at
)

store_analytics (
  id, store_id, date, views, orders,
  revenue, conversion_rate
)
```

## API Design

### RESTful Endpoints

#### Authentication ✅ **IMPLEMENTED**
```
POST   /api/auth/register           ✅ - User registration with email verification
POST   /api/auth/login              ✅ - JWT-based login with refresh tokens
POST   /api/auth/logout             ✅ - Secure logout
POST   /api/auth/refresh            ✅ - Token refresh mechanism
POST   /api/auth/forgot-password    ✅ - Password reset initiation
POST   /api/auth/reset-password     ✅ - Password reset completion
POST   /api/auth/verify-email       ✅ - Email verification
POST   /api/auth/resend-verification ✅ - Resend verification email
```

#### Users ✅ **IMPLEMENTED**
```
GET    /api/users/profile           ✅ - Get current user profile
PUT    /api/users/profile           ✅ - Update user profile
GET    /api/users/{id}              ✅ - Get user by ID (admin/self)
PUT    /api/users/{id}/status       ✅ - Update user status (admin only)
DELETE /api/users/{id}              ✅ - Delete user (admin/self)

# Address Management
GET    /api/users/me/addresses      ✅ - Get current user addresses
GET    /api/users/{id}/addresses    ✅ - Get user addresses
POST   /api/users/me/addresses      ✅ - Add address for current user
POST   /api/users/{id}/addresses    ✅ - Add address for user
PUT    /api/users/{id}/addresses/{addressId}    ✅ - Update address
DELETE /api/users/{id}/addresses/{addressId}    ✅ - Delete address
PUT    /api/users/{id}/addresses/{addressId}/default  ✅ - Set default address
GET    /api/users/{id}/addresses/default       ✅ - Get default address
```

#### Products
```
GET    /api/products
GET    /api/products/{id}
GET    /api/products/search
GET    /api/categories
GET    /api/categories/{id}/products
POST   /api/products/{id}/reviews
GET    /api/products/{id}/reviews
```

#### Cart & Orders
```
GET    /api/cart
POST   /api/cart/items
PUT    /api/cart/items/{id}
DELETE /api/cart/items/{id}
POST   /api/orders
GET    /api/orders
GET    /api/orders/{id}
PUT    /api/orders/{id}/cancel
```

#### Seller APIs
```
POST   /api/seller/products
PUT    /api/seller/products/{id}
DELETE /api/seller/products/{id}
GET    /api/seller/orders
PUT    /api/seller/orders/{id}/ship
GET    /api/seller/analytics
```

### GraphQL Schema

```graphql
type User {
  id: ID!
  email: String!
  profile: UserProfile
  orders: [Order!]!
  cart: Cart
}

type Product {
  id: ID!
  name: String!
  description: String
  price: Float!
  images: [String!]!
  category: Category!
  reviews: [Review!]!
  averageRating: Float
}

type Order {
  id: ID!
  status: OrderStatus!
  items: [OrderItem!]!
  total: Float!
  createdAt: DateTime!
}

type Query {
  products(filter: ProductFilter, pagination: Pagination): ProductConnection!
  product(id: ID!): Product
  user: User
  orders: [Order!]!
}

type Mutation {
  addToCart(productId: ID!, quantity: Int!): Cart!
  createOrder(input: CreateOrderInput!): Order!
  updateProduct(id: ID!, input: ProductInput!): Product!
}
```

## Security Architecture

### Authentication & Authorization
- JWT-based authentication with refresh tokens
- Role-based access control (RBAC)
- OAuth 2.0 for third-party integrations
- Rate limiting to prevent abuse

### Data Protection
- Encryption at rest and in transit (TLS 1.3)
- PCI DSS compliance for payment processing
- GDPR compliance for data privacy
- Regular security audits and penetration testing

### API Security
- Input validation and sanitization
- SQL injection prevention
- XSS protection
- CSRF protection
- API rate limiting and throttling

## Performance & Scalability

### Caching Strategy
- **Redis**: Session storage, frequently accessed data
- **CDN**: Static assets (images, CSS, JS)
- **Application Cache**: Product catalogs, user preferences
- **Database Query Cache**: Expensive query results

### Database Optimization
- Read replicas for analytics and reporting
- Database sharding for large datasets
- Connection pooling
- Query optimization and indexing

### Horizontal Scaling
- Microservices architecture for independent scaling
- Load balancing with sticky sessions
- Auto-scaling based on metrics
- Container orchestration with Kubernetes

## Plugin Architecture

### Plugin System Design
```java
@Component
public interface EcommercePlugin {
    String getName();
    String getVersion();
    void initialize();
    void shutdown();
}

@Component
public interface PaymentPlugin extends EcommercePlugin {
    PaymentResult processPayment(PaymentRequest request);
    boolean supportsPaymentMethod(PaymentMethod method);
}

@Component
public interface ShippingPlugin extends EcommercePlugin {
    ShippingQuote calculateShipping(ShippingRequest request);
    TrackingInfo trackShipment(String trackingNumber);
}
```

### Plugin Registry
- Dynamic plugin loading and unloading
- Plugin dependency management
- Configuration management per plugin
- Plugin marketplace for community contributions

## Deployment Architecture

### Development Environment
```yaml
# docker-compose.dev.yml
services:
  backend:
    build: ./backend
    ports: ["8080:8080"]
    environment:
      - PROFILE=dev
    depends_on: [postgres, redis]
  
  frontend:
    build: ./frontend
    ports: ["3000:3000"]
    volumes: ["./frontend:/app"]
  
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: ecommerce_dev
  
  redis:
    image: redis:7-alpine
```

### Production Environment (Kubernetes)
```yaml
# k8s/backend-deployment.yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ecommerce-backend
spec:
  replicas: 3
  selector:
    matchLabels:
      app: ecommerce-backend
  template:
    spec:
      containers:
      - name: backend
        image: ecommerce/backend:latest
        ports:
        - containerPort: 8080
        env:
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: url
```

## Monitoring & Observability

### Metrics Collection
- **Application Metrics**: Request rates, response times, error rates
- **Business Metrics**: Orders, revenue, conversion rates
- **Infrastructure Metrics**: CPU, memory, disk usage
- **Database Metrics**: Query performance, connection pools

### Logging Strategy
- Structured logging with JSON format
- Centralized log aggregation (ELK Stack)
- Log levels: ERROR, WARN, INFO, DEBUG
- Request correlation IDs for tracing

### Health Checks
```java
@Component
public class EcommerceHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // Check database connectivity
        // Check external service availability
        // Check cache connectivity
        return Health.up()
            .withDetail("database", "UP")
            .withDetail("redis", "UP")
            .build();
    }
}
```

## Internationalization

### Multi-Language Support
- Resource bundles for backend messages
- React i18n for frontend localization
- Database content translation
- RTL (Right-to-Left) language support

### Multi-Currency Support
- Real-time currency conversion APIs
- Locale-specific price formatting
- Tax calculation per region
- Multi-currency payment processing

### Timezone Management
- UTC storage with timezone conversion
- User timezone preferences
- Locale-specific date/time formatting

## Future Considerations

### AI/ML Integration
- Recommendation engine
- Dynamic pricing algorithms
- Fraud detection
- Inventory optimization

### Advanced Features
- Real-time chat and video streaming
- Augmented reality product viewing
- Voice commerce integration
- Blockchain for supply chain transparency

### Scalability Improvements
- Event-driven architecture
- CQRS (Command Query Responsibility Segregation)
- Event sourcing for audit trails
- Multi-region deployment