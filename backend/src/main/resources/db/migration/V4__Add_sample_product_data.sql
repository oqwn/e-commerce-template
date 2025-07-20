-- Insert sample categories
INSERT INTO categories (name, slug, description, parent_id, display_order) VALUES
('Electronics', 'electronics', 'Electronic devices and accessories', NULL, 1),
('Computers & Laptops', 'computers-laptops', 'Desktop computers, laptops, and accessories', 1, 1),
('Smartphones & Tablets', 'smartphones-tablets', 'Mobile phones and tablet devices', 1, 2),
('Fashion', 'fashion', 'Clothing, shoes, and accessories', NULL, 2),
('Men\'s Fashion', 'mens-fashion', 'Men\'s clothing and accessories', 4, 1),
('Women\'s Fashion', 'womens-fashion', 'Women\'s clothing and accessories', 4, 2),
('Home & Garden', 'home-garden', 'Home decor, furniture, and garden supplies', NULL, 3),
('Sports & Outdoors', 'sports-outdoors', 'Sports equipment and outdoor gear', NULL, 4),
('Books', 'books', 'Physical and digital books', NULL, 5),
('Toys & Games', 'toys-games', 'Toys, games, and hobbies', NULL, 6);

-- Get seller user ID (assuming seller@ecommerce.com exists from previous migration)
SET @seller_id = (SELECT id FROM users WHERE email = 'seller@ecommerce.com');

-- Insert sample products
INSERT INTO products (seller_id, category_id, name, slug, description, short_description, sku, price, compare_at_price, quantity, status, featured) VALUES
(@seller_id, 2, 'MacBook Pro 16-inch', 'macbook-pro-16-inch', 'The most powerful MacBook Pro ever is here. With the blazing-fast M3 Max chip, stunning Liquid Retina XDR display, and all-day battery life.', 'Powerful laptop with M3 Max chip', 'MBP16-M3-2024', 2499.00, 2799.00, 10, 'ACTIVE', 1),
(@seller_id, 2, 'Dell XPS 15', 'dell-xps-15', 'The XPS 15 is our most powerful laptop. With 13th Gen Intel Core processors and optional NVIDIA GeForce RTX 4070.', '15.6" laptop with Intel Core i7', 'DELL-XPS15-2024', 1899.00, NULL, 15, 'ACTIVE', 1),
(@seller_id, 3, 'iPhone 15 Pro', 'iphone-15-pro', 'iPhone 15 Pro. Forged in titanium. A17 Pro chip. Action button. The most powerful iPhone camera system.', 'Latest iPhone with titanium design', 'IP15PRO-128GB', 999.00, 1099.00, 25, 'ACTIVE', 1),
(@seller_id, 3, 'Samsung Galaxy S24 Ultra', 'samsung-galaxy-s24-ultra', 'Meet Galaxy S24 Ultra, the ultimate smartphone with built-in S Pen, 200MP camera, and all-day battery.', 'Premium Android smartphone', 'SGS24U-256GB', 1199.00, NULL, 20, 'ACTIVE', 0),
(@seller_id, 5, 'Classic Cotton T-Shirt', 'classic-cotton-tshirt', '100% premium cotton t-shirt. Comfortable, breathable, and perfect for everyday wear. Available in multiple colors.', 'Comfortable cotton t-shirt', 'TSH-COT-BLK-M', 29.99, 39.99, 100, 'ACTIVE', 0),
(@seller_id, 6, 'Elegant Summer Dress', 'elegant-summer-dress', 'Beautiful floral print summer dress. Made from lightweight, breathable fabric. Perfect for casual outings or beach days.', 'Floral print summer dress', 'DRS-SUM-FLR-M', 79.99, 99.99, 50, 'ACTIVE', 1),
(@seller_id, 7, 'Modern Coffee Table', 'modern-coffee-table', 'Sleek and modern coffee table with tempered glass top and wooden legs. Perfect centerpiece for your living room.', 'Glass-top coffee table', 'FRN-COF-MOD-01', 299.99, 399.99, 5, 'ACTIVE', 0),
(@seller_id, 8, 'Professional Yoga Mat', 'professional-yoga-mat', 'High-density yoga mat with excellent grip and cushioning. Non-slip surface, eco-friendly materials.', 'Non-slip yoga mat', 'YGA-MAT-PRO-BLU', 49.99, NULL, 30, 'ACTIVE', 0),
(@seller_id, 9, 'The Great Adventure', 'the-great-adventure-book', 'An epic tale of courage and discovery. Join our heroes on their journey across unknown lands.', 'Bestselling adventure novel', 'BK-ADV-2024', 24.99, 29.99, 75, 'ACTIVE', 0),
(@seller_id, 10, 'Strategy Board Game', 'strategy-board-game', 'Engaging strategy board game for 2-4 players. Build your empire and conquer your opponents!', 'Board game for ages 12+', 'GM-BRD-STR-01', 39.99, NULL, 40, 'ACTIVE', 0);

-- Insert product images
INSERT INTO product_images (product_id, image_url, alt_text, display_order, is_primary) VALUES
(1, 'https://example.com/images/macbook-pro-1.jpg', 'MacBook Pro front view', 0, 1),
(1, 'https://example.com/images/macbook-pro-2.jpg', 'MacBook Pro side view', 1, 0),
(2, 'https://example.com/images/dell-xps-1.jpg', 'Dell XPS 15 front view', 0, 1),
(3, 'https://example.com/images/iphone-15-1.jpg', 'iPhone 15 Pro front', 0, 1),
(3, 'https://example.com/images/iphone-15-2.jpg', 'iPhone 15 Pro back', 1, 0),
(4, 'https://example.com/images/galaxy-s24-1.jpg', 'Galaxy S24 Ultra', 0, 1),
(5, 'https://example.com/images/tshirt-black.jpg', 'Black cotton t-shirt', 0, 1),
(6, 'https://example.com/images/summer-dress.jpg', 'Floral summer dress', 0, 1),
(7, 'https://example.com/images/coffee-table.jpg', 'Modern coffee table', 0, 1),
(8, 'https://example.com/images/yoga-mat.jpg', 'Professional yoga mat', 0, 1);

-- Insert product tags
INSERT INTO product_tags (product_id, tag) VALUES
(1, 'laptop'), (1, 'apple'), (1, 'professional'),
(2, 'laptop'), (2, 'dell'), (2, 'windows'),
(3, 'smartphone'), (3, 'apple'), (3, 'premium'),
(4, 'smartphone'), (4, 'samsung'), (4, 'android'),
(5, 'clothing'), (5, 'casual'), (5, 'cotton'),
(6, 'clothing'), (6, 'summer'), (6, 'dress'),
(7, 'furniture'), (7, 'living-room'), (7, 'modern'),
(8, 'fitness'), (8, 'yoga'), (8, 'exercise'),
(9, 'fiction'), (9, 'adventure'), (9, 'bestseller'),
(10, 'board-game'), (10, 'strategy'), (10, 'family');

-- Insert product attributes
INSERT INTO product_attributes (product_id, attribute_name, attribute_value) VALUES
(1, 'Processor', 'Apple M3 Max'),
(1, 'RAM', '32GB'),
(1, 'Storage', '1TB SSD'),
(2, 'Processor', 'Intel Core i7-13700H'),
(2, 'RAM', '16GB'),
(2, 'Storage', '512GB SSD'),
(3, 'Storage', '128GB'),
(3, 'Display', '6.1-inch Super Retina XDR'),
(4, 'Storage', '256GB'),
(4, 'Display', '6.8-inch Dynamic AMOLED 2X');

-- Insert product variants for t-shirt (different sizes and colors)
INSERT INTO product_variants (product_id, name, sku, price, quantity, display_order) VALUES
(5, 'Black - Small', 'TSH-COT-BLK-S', 29.99, 25, 0),
(5, 'Black - Medium', 'TSH-COT-BLK-M', 29.99, 25, 1),
(5, 'Black - Large', 'TSH-COT-BLK-L', 29.99, 25, 2),
(5, 'Black - XL', 'TSH-COT-BLK-XL', 29.99, 25, 3),
(5, 'White - Small', 'TSH-COT-WHT-S', 29.99, 25, 4),
(5, 'White - Medium', 'TSH-COT-WHT-M', 29.99, 25, 5),
(5, 'White - Large', 'TSH-COT-WHT-L', 29.99, 25, 6),
(5, 'White - XL', 'TSH-COT-WHT-XL', 29.99, 25, 7);

-- Insert variant options
INSERT INTO variant_options (variant_id, option_name, option_value) VALUES
(1, 'Color', 'Black'), (1, 'Size', 'Small'),
(2, 'Color', 'Black'), (2, 'Size', 'Medium'),
(3, 'Color', 'Black'), (3, 'Size', 'Large'),
(4, 'Color', 'Black'), (4, 'Size', 'XL'),
(5, 'Color', 'White'), (5, 'Size', 'Small'),
(6, 'Color', 'White'), (6, 'Size', 'Medium'),
(7, 'Color', 'White'), (7, 'Size', 'Large'),
(8, 'Color', 'White'), (8, 'Size', 'XL');

-- Get buyer user ID
SET @buyer_id = (SELECT id FROM users WHERE email = 'buyer@ecommerce.com');

-- Insert sample reviews
INSERT INTO product_reviews (product_id, user_id, rating, title, comment, is_verified_purchase, status) VALUES
(1, @buyer_id, 5, 'Amazing laptop!', 'The M3 Max chip is incredibly fast. Battery life is outstanding.', 1, 'APPROVED'),
(3, @buyer_id, 5, 'Best iPhone yet', 'The titanium design feels premium. Camera quality is exceptional.', 1, 'APPROVED'),
(5, @buyer_id, 4, 'Good quality t-shirt', 'Comfortable and fits well. The cotton is soft and breathable.', 1, 'APPROVED');

-- Insert sample wishlist items
INSERT INTO wishlist (user_id, product_id) VALUES
(@buyer_id, 1),
(@buyer_id, 6),
(@buyer_id, 7);

-- Insert sample inventory transactions
INSERT INTO inventory_transactions (product_id, transaction_type, quantity, reference_type, notes, created_by) VALUES
(1, 'STOCK_IN', 10, 'INITIAL', 'Initial inventory', @seller_id),
(2, 'STOCK_IN', 15, 'INITIAL', 'Initial inventory', @seller_id),
(3, 'STOCK_IN', 25, 'INITIAL', 'Initial inventory', @seller_id),
(4, 'STOCK_IN', 20, 'INITIAL', 'Initial inventory', @seller_id),
(5, 'STOCK_IN', 100, 'INITIAL', 'Initial inventory', @seller_id);