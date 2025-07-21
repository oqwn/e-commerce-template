-- Add store_id column to coupons table and rename value column
ALTER TABLE coupons 
  ADD COLUMN store_id BIGINT NULL,
  ADD COLUMN discount_value DECIMAL(10,2) NULL;

-- Migrate data from value to discount_value column
UPDATE coupons SET discount_value = value;

-- Drop the old value column and make discount_value NOT NULL
ALTER TABLE coupons 
  DROP COLUMN value,
  MODIFY COLUMN discount_value DECIMAL(10,2) NOT NULL;

-- Add foreign key constraint for store_id
ALTER TABLE coupons 
  ADD CONSTRAINT fk_coupon_store 
  FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE;

-- Add index for store_id
CREATE INDEX idx_coupon_store_id ON coupons(store_id);

-- Add used_at column to coupon_usages table
ALTER TABLE coupon_usages 
  ADD COLUMN used_at TIMESTAMP NULL;

-- Migrate data from created_at to used_at
UPDATE coupon_usages SET used_at = created_at;

-- Drop created_at column and make used_at have DEFAULT CURRENT_TIMESTAMP
ALTER TABLE coupon_usages 
  DROP COLUMN created_at,
  MODIFY COLUMN used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;