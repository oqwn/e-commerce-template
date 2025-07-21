-- Create stores table
CREATE TABLE IF NOT EXISTS stores (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    seller_id BIGINT NOT NULL UNIQUE,
    store_name VARCHAR(255) NOT NULL,
    store_slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    logo_url VARCHAR(500),
    banner_url VARCHAR(500),
    
    -- Store Information
    business_name VARCHAR(255),
    business_registration_number VARCHAR(100),
    tax_id VARCHAR(100),
    
    -- Contact Information
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),
    support_email VARCHAR(255),
    support_phone VARCHAR(20),
    
    -- Address
    street VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(2) DEFAULT 'US',
    
    -- Store Settings
    currency VARCHAR(3) DEFAULT 'USD',
    timezone VARCHAR(50) DEFAULT 'UTC',
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    is_verified TINYINT(1) NOT NULL DEFAULT 0,
    verification_status VARCHAR(20) DEFAULT 'PENDING',
    verification_date TIMESTAMP NULL,
    
    -- Store Policies
    return_policy TEXT,
    shipping_policy TEXT,
    privacy_policy TEXT,
    terms_and_conditions TEXT,
    
    -- Social Media
    website_url VARCHAR(500),
    facebook_url VARCHAR(500),
    instagram_url VARCHAR(500),
    twitter_url VARCHAR(500),
    youtube_url VARCHAR(500),
    
    -- Store Metrics
    rating DECIMAL(3,2) DEFAULT 0.00,
    total_reviews INT DEFAULT 0,
    total_products INT DEFAULT 0,
    total_sales INT DEFAULT 0,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_stores_seller FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_stores_verification_status CHECK (verification_status IN ('PENDING', 'IN_REVIEW', 'APPROVED', 'REJECTED', 'SUSPENDED')),
    CONSTRAINT chk_stores_rating CHECK (rating >= 0 AND rating <= 5),
    INDEX idx_stores_slug (store_slug),
    INDEX idx_stores_active (is_active),
    INDEX idx_stores_verified (is_verified),
    INDEX idx_stores_rating (rating)
);

-- Create store_categories table (categories that store specializes in)
CREATE TABLE IF NOT EXISTS store_categories (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    is_primary TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_store_categories_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT fk_store_categories_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    UNIQUE KEY uk_store_categories (store_id, category_id),
    INDEX idx_store_categories_store (store_id),
    INDEX idx_store_categories_category (category_id)
);

-- Create store_customization table
CREATE TABLE IF NOT EXISTS store_customization (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL UNIQUE,
    
    -- Theme Settings
    theme_name VARCHAR(50) DEFAULT 'default',
    primary_color VARCHAR(7) DEFAULT '#3B82F6',
    secondary_color VARCHAR(7) DEFAULT '#10B981',
    accent_color VARCHAR(7) DEFAULT '#F59E0B',
    background_color VARCHAR(7) DEFAULT '#FFFFFF',
    text_color VARCHAR(7) DEFAULT '#1F2937',
    
    -- Layout Settings
    layout_type VARCHAR(20) DEFAULT 'GRID',
    products_per_page INT DEFAULT 20,
    show_banner TINYINT(1) DEFAULT 1,
    show_featured_products TINYINT(1) DEFAULT 1,
    show_categories TINYINT(1) DEFAULT 1,
    
    -- Custom CSS
    custom_css TEXT,
    
    -- SEO Settings
    meta_title VARCHAR(255),
    meta_description TEXT,
    meta_keywords TEXT,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_store_customization_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT chk_layout_type CHECK (layout_type IN ('GRID', 'LIST', 'MASONRY'))
);

-- Create store_analytics table
CREATE TABLE IF NOT EXISTS store_analytics (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    date DATE NOT NULL,
    
    -- Traffic Metrics
    total_visits INT DEFAULT 0,
    unique_visitors INT DEFAULT 0,
    page_views INT DEFAULT 0,
    bounce_rate DECIMAL(5,2) DEFAULT 0.00,
    avg_session_duration INT DEFAULT 0, -- in seconds
    
    -- Sales Metrics
    total_orders INT DEFAULT 0,
    total_revenue DECIMAL(12,2) DEFAULT 0.00,
    avg_order_value DECIMAL(10,2) DEFAULT 0.00,
    conversion_rate DECIMAL(5,2) DEFAULT 0.00,
    
    -- Product Metrics
    products_viewed INT DEFAULT 0,
    products_added_to_cart INT DEFAULT 0,
    products_purchased INT DEFAULT 0,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_store_analytics_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    UNIQUE KEY uk_store_analytics_date (store_id, date),
    INDEX idx_store_analytics_store (store_id),
    INDEX idx_store_analytics_date (date)
);

-- Create store_reviews table
CREATE TABLE IF NOT EXISTS store_reviews (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    order_id BIGINT, -- Will be linked when orders table is created
    rating INT NOT NULL,
    title VARCHAR(255),
    comment TEXT,
    seller_response TEXT,
    seller_response_date TIMESTAMP NULL,
    is_verified_purchase TINYINT(1) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_store_reviews_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT fk_store_reviews_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_store_reviews_rating CHECK (rating >= 1 AND rating <= 5),
    CONSTRAINT chk_store_reviews_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    UNIQUE KEY uk_store_reviews (store_id, user_id, order_id),
    INDEX idx_store_reviews_store (store_id),
    INDEX idx_store_reviews_user (user_id),
    INDEX idx_store_reviews_status (status),
    INDEX idx_store_reviews_rating (rating)
);

-- Create store_operating_hours table
CREATE TABLE IF NOT EXISTS store_operating_hours (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    day_of_week INT NOT NULL, -- 0=Sunday, 1=Monday, ..., 6=Saturday
    open_time TIME,
    close_time TIME,
    is_closed TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_store_operating_hours_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT chk_day_of_week CHECK (day_of_week >= 0 AND day_of_week <= 6),
    UNIQUE KEY uk_store_operating_hours (store_id, day_of_week),
    INDEX idx_store_operating_hours_store (store_id)
);

-- Create store_verification_documents table
CREATE TABLE IF NOT EXISTS store_verification_documents (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    document_url VARCHAR(500) NOT NULL,
    document_name VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reviewer_notes TEXT,
    reviewed_by BIGINT,
    reviewed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_store_verification_docs_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT fk_store_verification_docs_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_document_type CHECK (document_type IN ('BUSINESS_LICENSE', 'TAX_CERTIFICATE', 'ID_PROOF', 'ADDRESS_PROOF', 'OTHER')),
    CONSTRAINT chk_document_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    INDEX idx_store_verification_docs_store (store_id),
    INDEX idx_store_verification_docs_status (status)
);

-- Add store_id to products table to link products to stores
ALTER TABLE products ADD COLUMN store_id BIGINT;
ALTER TABLE products ADD CONSTRAINT fk_products_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE;
ALTER TABLE products ADD INDEX idx_products_store (store_id);

-- Create trigger to update store_id when product is created
DELIMITER $$
CREATE TRIGGER set_product_store_id
BEFORE INSERT ON products
FOR EACH ROW
BEGIN
    IF NEW.store_id IS NULL THEN
        SET NEW.store_id = (SELECT id FROM stores WHERE seller_id = NEW.seller_id LIMIT 1);
    END IF;
END$$
DELIMITER ;

-- Create trigger to update store metrics
DELIMITER $$
CREATE TRIGGER update_store_product_count_on_insert
AFTER INSERT ON products
FOR EACH ROW
BEGIN
    UPDATE stores 
    SET total_products = (
        SELECT COUNT(*) FROM products 
        WHERE store_id = NEW.store_id AND status = 'ACTIVE'
    )
    WHERE id = NEW.store_id;
END$$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER update_store_product_count_on_update
AFTER UPDATE ON products
FOR EACH ROW
BEGIN
    IF NEW.status != OLD.status OR NEW.store_id != OLD.store_id THEN
        -- Update old store
        IF OLD.store_id IS NOT NULL THEN
            UPDATE stores 
            SET total_products = (
                SELECT COUNT(*) FROM products 
                WHERE store_id = OLD.store_id AND status = 'ACTIVE'
            )
            WHERE id = OLD.store_id;
        END IF;
        
        -- Update new store
        IF NEW.store_id IS NOT NULL THEN
            UPDATE stores 
            SET total_products = (
                SELECT COUNT(*) FROM products 
                WHERE store_id = NEW.store_id AND status = 'ACTIVE'
            )
            WHERE id = NEW.store_id;
        END IF;
    END IF;
END$$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER update_store_product_count_on_delete
AFTER DELETE ON products
FOR EACH ROW
BEGIN
    UPDATE stores 
    SET total_products = (
        SELECT COUNT(*) FROM products 
        WHERE store_id = OLD.store_id AND status = 'ACTIVE'
    )
    WHERE id = OLD.store_id;
END$$
DELIMITER ;

-- Create trigger to update store rating
DELIMITER $$
CREATE TRIGGER update_store_rating_on_review
AFTER INSERT ON store_reviews
FOR EACH ROW
BEGIN
    IF NEW.status = 'APPROVED' THEN
        UPDATE stores 
        SET rating = (
            SELECT AVG(rating) FROM store_reviews 
            WHERE store_id = NEW.store_id AND status = 'APPROVED'
        ),
        total_reviews = (
            SELECT COUNT(*) FROM store_reviews 
            WHERE store_id = NEW.store_id AND status = 'APPROVED'
        )
        WHERE id = NEW.store_id;
    END IF;
END$$
DELIMITER ;