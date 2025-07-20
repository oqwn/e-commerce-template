-- Insert sample users (password is 'password' for all)
-- BCrypt hash for 'password': $2a$10$zRjAoMZkzZRI0kgU0jUdquTnBa1UJo96qGiiU0ionKngmAkeDHMja

-- Admin user
INSERT INTO users (email, password_hash, first_name, last_name, role, email_verified)
VALUES ('admin@ecommerce.com', '$2a$10$zRjAoMZkzZRI0kgU0jUdquTnBa1UJo96qGiiU0ionKngmAkeDHMja', 'Admin', 'User', 'ADMIN', 1);

-- Seller user
INSERT INTO users (email, password_hash, first_name, last_name, role, email_verified)
VALUES ('seller@ecommerce.com', '$2a$10$zRjAoMZkzZRI0kgU0jUdquTnBa1UJo96qGiiU0ionKngmAkeDHMja', 'Seller', 'User', 'SELLER', 1);

-- Buyer user
INSERT INTO users (email, password_hash, first_name, last_name, role, email_verified)
VALUES ('buyer@ecommerce.com', '$2a$10$zRjAoMZkzZRI0kgU0jUdquTnBa1UJo96qGiiU0ionKngmAkeDHMja', 'Buyer', 'User', 'BUYER', 1);

-- Add sample addresses for buyer
INSERT INTO addresses (user_id, type, first_name, last_name, street, city, state, postal_code, country, is_default)
SELECT id, 'HOME', 'Buyer', 'User', '123 Main St', 'New York', 'NY', '10001', 'US', 1
FROM users WHERE email = 'buyer@ecommerce.com';

INSERT INTO addresses (user_id, type, first_name, last_name, street, city, state, postal_code, country, is_default)
SELECT id, 'WORK', 'Buyer', 'User', '456 Office Ave', 'New York', 'NY', '10002', 'US', 0
FROM users WHERE email = 'buyer@ecommerce.com';