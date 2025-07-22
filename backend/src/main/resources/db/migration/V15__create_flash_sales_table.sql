-- Create flash_sales table
CREATE TABLE flash_sales (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    discount_percentage DECIMAL(5,2) NOT NULL CHECK (discount_percentage > 0 AND discount_percentage <= 100),
    max_quantity INT,
    used_quantity INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    
    CONSTRAINT fk_flash_sale_creator FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT chk_flash_sale_time CHECK (end_time > start_time),
    CONSTRAINT chk_max_quantity CHECK (max_quantity IS NULL OR max_quantity > 0),
    CONSTRAINT chk_used_quantity CHECK (used_quantity >= 0)
);

-- Create flash_sale_products junction table
CREATE TABLE flash_sale_products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    flash_sale_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    original_price DECIMAL(10,2) NOT NULL,
    sale_price DECIMAL(10,2) NOT NULL,
    max_quantity_per_product INT,
    used_quantity_per_product INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_flash_sale_product_sale FOREIGN KEY (flash_sale_id) REFERENCES flash_sales(id) ON DELETE CASCADE,
    CONSTRAINT fk_flash_sale_product_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT unique_flash_sale_product UNIQUE KEY (flash_sale_id, product_id),
    CONSTRAINT chk_sale_price CHECK (sale_price >= 0),
    CONSTRAINT chk_original_price CHECK (original_price >= 0),
    CONSTRAINT chk_max_qty_per_product CHECK (max_quantity_per_product IS NULL OR max_quantity_per_product > 0),
    CONSTRAINT chk_used_qty_per_product CHECK (used_quantity_per_product >= 0)
);

-- Create indexes for better performance
CREATE INDEX idx_flash_sales_active ON flash_sales(is_active);
CREATE INDEX idx_flash_sales_time ON flash_sales(start_time, end_time);
CREATE INDEX idx_flash_sales_creator ON flash_sales(created_by);
CREATE INDEX idx_flash_sale_products_sale ON flash_sale_products(flash_sale_id);
CREATE INDEX idx_flash_sale_products_product ON flash_sale_products(product_id);