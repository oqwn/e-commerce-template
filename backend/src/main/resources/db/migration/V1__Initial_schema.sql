-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL DEFAULT 'BUYER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    email_verified TINYINT(1) NOT NULL DEFAULT 0,
    email_verification_token VARCHAR(255),
    email_verification_expiry TIMESTAMP NULL,
    password_reset_token VARCHAR(255),
    password_reset_expiry TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_role CHECK (role IN ('BUYER', 'SELLER', 'ADMIN')),
    CONSTRAINT chk_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED'))
);

-- Create indexes for users table
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_email_verification_token ON users(email_verification_token);
CREATE INDEX idx_users_password_reset_token ON users(password_reset_token);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Create addresses table
CREATE TABLE IF NOT EXISTS addresses (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'HOME',
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    street VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(2) NOT NULL DEFAULT 'US',
    is_default TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_type CHECK (type IN ('HOME', 'WORK', 'OTHER'))
);

-- Create indexes for addresses table
CREATE INDEX idx_addresses_user_id ON addresses(user_id);
CREATE INDEX idx_addresses_user_id_default ON addresses(user_id, is_default);
CREATE INDEX idx_addresses_type ON addresses(type);

-- Note: MySQL automatically updates the updated_at field with ON UPDATE CURRENT_TIMESTAMP