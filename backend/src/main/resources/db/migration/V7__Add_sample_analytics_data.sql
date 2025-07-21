-- Add sample analytics data for the electronics store (store_id = 1)
-- Generate data for the last 30 days

DELIMITER $$

CREATE PROCEDURE generate_sample_analytics()
BEGIN
    DECLARE date_counter INT DEFAULT 0;
    DECLARE current_date DATE;
    
    WHILE date_counter < 30 DO
        SET current_date = DATE_SUB(CURDATE(), INTERVAL date_counter DAY);
        
        INSERT INTO store_analytics (
            store_id, date, total_visits, unique_visitors, page_views,
            bounce_rate, avg_session_duration, total_orders, total_revenue,
            avg_order_value, conversion_rate, products_viewed, 
            products_added_to_cart, products_purchased
        ) VALUES (
            1, -- Electronics Haven store
            current_date,
            FLOOR(100 + RAND() * 400), -- total_visits (100-500)
            FLOOR(80 + RAND() * 300), -- unique_visitors (80-380)
            FLOOR(300 + RAND() * 1200), -- page_views (300-1500)
            ROUND(30 + RAND() * 20, 2), -- bounce_rate (30-50%)
            FLOOR(120 + RAND() * 180), -- avg_session_duration (2-5 minutes in seconds)
            FLOOR(5 + RAND() * 20), -- total_orders (5-25)
            ROUND(500 + RAND() * 4500, 2), -- total_revenue ($500-$5000)
            ROUND(100 + RAND() * 200, 2), -- avg_order_value ($100-$300)
            ROUND(2 + RAND() * 6, 2), -- conversion_rate (2-8%)
            FLOOR(50 + RAND() * 200), -- products_viewed (50-250)
            FLOOR(10 + RAND() * 40), -- products_added_to_cart (10-50)
            FLOOR(5 + RAND() * 20) -- products_purchased (5-25)
        );
        
        SET date_counter = date_counter + 1;
    END WHILE;
END$$

DELIMITER ;

-- Execute the procedure
CALL generate_sample_analytics();

-- Drop the procedure
DROP PROCEDURE generate_sample_analytics;

-- Add some better recent data for demonstration
UPDATE store_analytics 
SET total_revenue = 3250.50,
    total_orders = 18,
    avg_order_value = 180.58,
    conversion_rate = 5.2
WHERE store_id = 1 AND date = CURDATE();

UPDATE store_analytics 
SET total_revenue = 2890.00,
    total_orders = 15,
    avg_order_value = 192.67,
    conversion_rate = 4.8
WHERE store_id = 1 AND date = DATE_SUB(CURDATE(), INTERVAL 1 DAY);

UPDATE store_analytics 
SET total_revenue = 4120.75,
    total_orders = 22,
    avg_order_value = 187.31,
    conversion_rate = 6.1
WHERE store_id = 1 AND date = DATE_SUB(CURDATE(), INTERVAL 2 DAY);