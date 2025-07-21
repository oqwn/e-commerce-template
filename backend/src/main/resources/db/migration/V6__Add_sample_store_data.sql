-- Insert sample store for the seller user
INSERT INTO stores (
    seller_id,
    store_name,
    store_slug,
    description,
    business_name,
    contact_email,
    contact_phone,
    street,
    city,
    state,
    postal_code,
    country,
    is_active,
    is_verified,
    verification_status,
    verification_date,
    rating,
    total_reviews,
    total_products
) VALUES (
    (SELECT id FROM users WHERE email = 'seller@ecommerce.com'),
    'TechGadget Store',
    'techgadget-store',
    'Your one-stop shop for the latest technology gadgets and accessories. We offer high-quality products at competitive prices with excellent customer service.',
    'TechGadget Inc.',
    'contact@techgadget.com',
    '+1-555-0123',
    '123 Tech Street',
    'San Francisco',
    'CA',
    '94105',
    'US',
    1,
    1,
    'APPROVED',
    NOW(),
    4.5,
    127,
    0  -- Will be updated by trigger
);

-- Add store categories
INSERT INTO store_categories (store_id, category_id, is_primary)
SELECT 
    s.id,
    c.id,
    CASE WHEN c.slug = 'electronics' THEN 1 ELSE 0 END
FROM stores s
CROSS JOIN categories c
WHERE s.store_slug = 'techgadget-store' 
AND c.slug IN ('electronics', 'smartphones', 'laptops', 'accessories', 'audio');

-- Insert store customization
INSERT INTO store_customization (
    store_id,
    theme_name,
    primary_color,
    secondary_color,
    accent_color,
    layout_type,
    products_per_page,
    show_banner,
    show_featured_products,
    show_categories,
    meta_title,
    meta_description,
    meta_keywords
) VALUES (
    (SELECT id FROM stores WHERE store_slug = 'techgadget-store'),
    'modern',
    '#2563EB',
    '#10B981',
    '#F59E0B',
    'GRID',
    24,
    1,
    1,
    1,
    'TechGadget Store - Latest Technology Gadgets & Accessories',
    'Shop the latest technology gadgets and accessories at TechGadget Store. Find smartphones, laptops, audio equipment, and more at competitive prices.',
    'technology, gadgets, electronics, smartphones, laptops, accessories, tech store'
);

-- Insert operating hours (Monday to Saturday 9 AM to 6 PM, Sunday closed)
INSERT INTO store_operating_hours (store_id, day_of_week, open_time, close_time, is_closed)
SELECT 
    (SELECT id FROM stores WHERE store_slug = 'techgadget-store'),
    day_num,
    CASE WHEN day_num = 0 THEN NULL ELSE '09:00:00' END,
    CASE WHEN day_num = 0 THEN NULL ELSE '18:00:00' END,
    CASE WHEN day_num = 0 THEN 1 ELSE 0 END
FROM (
    SELECT 0 as day_num UNION ALL
    SELECT 1 UNION ALL
    SELECT 2 UNION ALL
    SELECT 3 UNION ALL
    SELECT 4 UNION ALL
    SELECT 5 UNION ALL
    SELECT 6
) days;

-- Insert sample store analytics for the last 7 days
INSERT INTO store_analytics (
    store_id,
    date,
    total_visits,
    unique_visitors,
    page_views,
    bounce_rate,
    avg_session_duration,
    total_orders,
    total_revenue,
    avg_order_value,
    conversion_rate,
    products_viewed,
    products_added_to_cart,
    products_purchased
)
SELECT 
    (SELECT id FROM stores WHERE store_slug = 'techgadget-store'),
    DATE_SUB(CURDATE(), INTERVAL days.n DAY),
    FLOOR(RAND() * 500 + 100),  -- 100-600 visits
    FLOOR(RAND() * 300 + 80),   -- 80-380 unique visitors
    FLOOR(RAND() * 1500 + 300), -- 300-1800 page views
    ROUND(RAND() * 30 + 20, 2), -- 20-50% bounce rate
    FLOOR(RAND() * 300 + 120),  -- 120-420 seconds avg session
    FLOOR(RAND() * 20 + 5),     -- 5-25 orders
    ROUND(RAND() * 3000 + 500, 2), -- $500-$3500 revenue
    ROUND(RAND() * 100 + 50, 2),   -- $50-$150 avg order value
    ROUND(RAND() * 5 + 1, 2),      -- 1-6% conversion rate
    FLOOR(RAND() * 800 + 200),     -- 200-1000 products viewed
    FLOOR(RAND() * 100 + 20),      -- 20-120 added to cart
    FLOOR(RAND() * 50 + 10)        -- 10-60 purchased
FROM (
    SELECT 0 as n UNION ALL
    SELECT 1 UNION ALL
    SELECT 2 UNION ALL
    SELECT 3 UNION ALL
    SELECT 4 UNION ALL
    SELECT 5 UNION ALL
    SELECT 6
) days;

-- Insert sample store reviews
INSERT INTO store_reviews (
    store_id,
    user_id,
    rating,
    title,
    comment,
    is_verified_purchase,
    status,
    created_at
) VALUES
(
    (SELECT id FROM stores WHERE store_slug = 'techgadget-store'),
    (SELECT id FROM users WHERE email = 'buyer@ecommerce.com'),
    5,
    'Excellent service and fast shipping!',
    'I ordered a laptop from TechGadget Store and it arrived the next day. The product was exactly as described and the customer service was outstanding. Highly recommended!',
    1,
    'APPROVED',
    DATE_SUB(NOW(), INTERVAL 7 DAY)
),
(
    (SELECT id FROM stores WHERE store_slug = 'techgadget-store'),
    (SELECT id FROM users WHERE email = 'admin@ecommerce.com'),
    4,
    'Good selection, fair prices',
    'TechGadget Store has a wide variety of products. Prices are competitive and the website is easy to navigate. Only minor issue was a slight delay in shipping, but overall a good experience.',
    1,
    'APPROVED',
    DATE_SUB(NOW(), INTERVAL 14 DAY)
);

-- Insert verification documents (already approved)
INSERT INTO store_verification_documents (
    store_id,
    document_type,
    document_url,
    document_name,
    status,
    reviewer_notes,
    reviewed_by,
    reviewed_at
) VALUES
(
    (SELECT id FROM stores WHERE store_slug = 'techgadget-store'),
    'BUSINESS_LICENSE',
    '/uploads/stores/techgadget/business-license.pdf',
    'Business License - TechGadget Inc.pdf',
    'APPROVED',
    'All documents verified and approved.',
    (SELECT id FROM users WHERE email = 'admin@ecommerce.com'),
    DATE_SUB(NOW(), INTERVAL 30 DAY)
),
(
    (SELECT id FROM stores WHERE store_slug = 'techgadget-store'),
    'TAX_CERTIFICATE',
    '/uploads/stores/techgadget/tax-certificate.pdf',
    'Tax Registration Certificate.pdf',
    'APPROVED',
    'Tax certificate valid and verified.',
    (SELECT id FROM users WHERE email = 'admin@ecommerce.com'),
    DATE_SUB(NOW(), INTERVAL 30 DAY)
);

-- Update existing products to link them to the store
UPDATE products p
SET p.store_id = (SELECT id FROM stores WHERE seller_id = p.seller_id)
WHERE p.seller_id = (SELECT id FROM users WHERE email = 'seller@ecommerce.com');

-- Add social media links and policies to the store
UPDATE stores
SET 
    return_policy = 'We offer a 30-day return policy for all products. Items must be in original condition with all accessories and packaging. Refunds will be processed within 5-7 business days after receiving the returned item.',
    shipping_policy = 'We ship all orders within 1-2 business days. Standard shipping takes 3-5 business days. Express shipping (1-2 days) is available for an additional fee. Free shipping on orders over $50.',
    privacy_policy = 'We respect your privacy and protect your personal information. We do not share your data with third parties without your consent. All transactions are secured with SSL encryption.',
    terms_and_conditions = 'By purchasing from TechGadget Store, you agree to our terms of service. All products come with manufacturer warranty. We are not responsible for misuse or damage caused by improper handling.',
    website_url = 'https://www.techgadget.com',
    facebook_url = 'https://facebook.com/techgadget',
    instagram_url = 'https://instagram.com/techgadget',
    twitter_url = 'https://twitter.com/techgadget'
WHERE store_slug = 'techgadget-store';