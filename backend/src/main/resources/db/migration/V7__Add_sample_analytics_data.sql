-- Add sample analytics data for the electronics store
-- Generate data for the last 30 days using simple INSERT statements
-- Only insert if no analytics data exists for this store

-- Insert sample store analytics for the last 30 days (only if none exist)
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
    SELECT 6 UNION ALL
    SELECT 7 UNION ALL
    SELECT 8 UNION ALL
    SELECT 9 UNION ALL
    SELECT 10 UNION ALL
    SELECT 11 UNION ALL
    SELECT 12 UNION ALL
    SELECT 13 UNION ALL
    SELECT 14 UNION ALL
    SELECT 15 UNION ALL
    SELECT 16 UNION ALL
    SELECT 17 UNION ALL
    SELECT 18 UNION ALL
    SELECT 19 UNION ALL
    SELECT 20 UNION ALL
    SELECT 21 UNION ALL
    SELECT 22 UNION ALL
    SELECT 23 UNION ALL
    SELECT 24 UNION ALL
    SELECT 25 UNION ALL
    SELECT 26 UNION ALL
    SELECT 27 UNION ALL
    SELECT 28 UNION ALL
    SELECT 29
) days
WHERE (SELECT id FROM stores WHERE store_slug = 'techgadget-store') IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM store_analytics sa 
    WHERE sa.store_id = (SELECT id FROM stores WHERE store_slug = 'techgadget-store')
      AND sa.date = DATE_SUB(CURDATE(), INTERVAL days.n DAY)
  );

-- Add some specific high-quality data for recent days
UPDATE store_analytics 
SET total_revenue = 3250.50,
    total_orders = 18,
    avg_order_value = 180.58,
    conversion_rate = 5.2,
    total_visits = 450,
    unique_visitors = 320,
    page_views = 1250
WHERE store_id = (SELECT id FROM stores WHERE store_slug = 'techgadget-store') 
  AND date = CURDATE();

UPDATE store_analytics 
SET total_revenue = 2890.00,
    total_orders = 15,
    avg_order_value = 192.67,
    conversion_rate = 4.8,
    total_visits = 380,
    unique_visitors = 290,
    page_views = 1100
WHERE store_id = (SELECT id FROM stores WHERE store_slug = 'techgadget-store') 
  AND date = DATE_SUB(CURDATE(), INTERVAL 1 DAY);

UPDATE store_analytics 
SET total_revenue = 4120.75,
    total_orders = 22,
    avg_order_value = 187.31,
    conversion_rate = 6.1,
    total_visits = 520,
    unique_visitors = 410,
    page_views = 1450
WHERE store_id = (SELECT id FROM stores WHERE store_slug = 'techgadget-store') 
  AND date = DATE_SUB(CURDATE(), INTERVAL 2 DAY);