-- Script tạo dữ liệu test: Product APPROVED và Order CONFIRMED
-- Để test: Retailer xem marketplace, Retailer xem order, Farm-manager xem order, Shipping-manager nhận shipment

USE bicap_order_db;

-- 1. Tạo Farm Manager (nếu chưa có)
INSERT INTO farm_manager (id, username, email, role, farm_id) 
VALUES (1, 'farmmanager1', 'farm@manager.com', 'ROLE_FARMMANAGER', 1)
ON DUPLICATE KEY UPDATE username=username, email='farm@manager.com', farm_id=1;

-- 2. Tạo Marketplace Product với status APPROVED (để hiển thị trên marketplace)
INSERT INTO marketplace_products (
    id, name, category, description, quantity, unit, price, 
    image_url, batch_id, status, created_at, farm_manager_id
) VALUES (
    1, 
    'Cà chua sạch Đà Lạt', 
    'Rau củ', 
    'Cà chua sạch được trồng tại Đà Lạt, không sử dụng thuốc trừ sâu, đảm bảo chất lượng', 
    100, 
    'kg', 
    50000.00, 
    '/assets/img/product-placeholder.jpg', 
    'BATCH001', 
    'APPROVED', 
    NOW(), 
    1
)
ON DUPLICATE KEY UPDATE 
    status='APPROVED',
    name='Cà chua sạch Đà Lạt',
    quantity=100,
    price=50000.00;

-- 3. Tạo thêm product khác để test
INSERT INTO marketplace_products (
    id, name, category, description, quantity, unit, price, 
    image_url, batch_id, status, created_at, farm_manager_id
) VALUES (
    2, 
    'Rau cải xanh', 
    'Rau củ', 
    'Rau cải xanh tươi ngon, trồng theo phương pháp hữu cơ', 
    50, 
    'kg', 
    30000.00, 
    '/assets/img/product-placeholder.jpg', 
    'BATCH002', 
    'APPROVED', 
    NOW(), 
    1
)
ON DUPLICATE KEY UPDATE 
    status='APPROVED',
    name='Rau cải xanh',
    quantity=50,
    price=30000.00;

-- 4. Tạo Order với status CONFIRMED (để test retailer xem order của mình)
INSERT INTO orders (
    id, buyer_id, buyer_email, shipping_address, total_amount, status, created_at
) VALUES (
    1,
    2, -- buyer_id (retailer user ID)
    'retailer@test.com',
    '123 Đường Test, Quận 1, TP. Hồ Chí Minh',
    500000.00,
    'CONFIRMED',
    NOW()
)
ON DUPLICATE KEY UPDATE 
    status='CONFIRMED',
    buyer_email='retailer@test.com',
    total_amount=500000.00;

-- 5. Tạo Order Items cho order 1
INSERT INTO order_items (
    id, order_id, product_id, quantity, unit_price
) VALUES (
    1,
    1,
    1, -- Product: Cà chua sạch Đà Lạt
    10, -- 10kg
    50000.00
)
ON DUPLICATE KEY UPDATE 
    quantity=10,
    unit_price=50000.00;

-- 6. Tạo Order thứ 2 CONFIRMED (để có nhiều dữ liệu test)
INSERT INTO orders (
    id, buyer_id, buyer_email, shipping_address, total_amount, status, created_at
) VALUES (
    2,
    2, -- buyer_id (retailer user ID)
    'retailer@test.com',
    '456 Đường ABC, Quận 3, TP. Hồ Chí Minh',
    300000.00,
    'CONFIRMED',
    DATE_SUB(NOW(), INTERVAL 1 DAY)
)
ON DUPLICATE KEY UPDATE 
    status='CONFIRMED',
    buyer_email='retailer@test.com',
    total_amount=300000.00;

-- 7. Tạo Order Items cho order 2
INSERT INTO order_items (
    id, order_id, product_id, quantity, unit_price
) VALUES (
    2,
    2,
    2, -- Product: Rau cải xanh
    10, -- 10kg
    30000.00
)
ON DUPLICATE KEY UPDATE 
    quantity=10,
    unit_price=30000.00;

-- ============================================
-- Shipping Database
-- ============================================

USE shipping_db;

-- 8. Tạo Shipment cho order 1 (đã CONFIRMED)
INSERT INTO shipments (
    order_id, status, from_location, to_location, created_date, updated_date
) VALUES (
    1,
    'PENDING',
    'Trang trại Đà Lạt, Lâm Đồng',
    '123 Đường Test, Quận 1, TP. Hồ Chí Minh',
    NOW(),
    NOW()
)
ON DUPLICATE KEY UPDATE 
    status='PENDING',
    from_location='Trang trại Đà Lạt, Lâm Đồng',
    to_location='123 Đường Test, Quận 1, TP. Hồ Chí Minh';

-- 9. Tạo Shipment cho order 2
INSERT INTO shipments (
    order_id, status, from_location, to_location, created_date, updated_date
) VALUES (
    2,
    'PENDING',
    'Trang trại Đà Lạt, Lâm Đồng',
    '456 Đường ABC, Quận 3, TP. Hồ Chí Minh',
    DATE_SUB(NOW(), INTERVAL 1 DAY),
    DATE_SUB(NOW(), INTERVAL 1 DAY)
)
ON DUPLICATE KEY UPDATE 
    status='PENDING',
    from_location='Trang trại Đà Lạt, Lâm Đồng',
    to_location='456 Đường ABC, Quận 3, TP. Hồ Chí Minh';

-- ============================================
-- Kiểm tra dữ liệu đã tạo
-- ============================================

USE bicap_order_db;
SELECT '=== PRODUCTS (APPROVED) ===' AS '';
SELECT id, name, status, price, quantity, unit FROM marketplace_products WHERE status = 'APPROVED';

SELECT '=== ORDERS (CONFIRMED) ===' AS '';
SELECT id, buyer_email, status, total_amount, shipping_address, created_at FROM orders WHERE status = 'CONFIRMED';

SELECT '=== ORDER ITEMS ===' AS '';
SELECT oi.id, oi.order_id, oi.product_id, mp.name as product_name, oi.quantity, oi.unit_price 
FROM order_items oi 
JOIN marketplace_products mp ON oi.product_id = mp.id 
WHERE oi.order_id IN (1, 2);

USE shipping_db;
SELECT '=== SHIPMENTS ===' AS '';
SELECT id, order_id, status, from_location, to_location, created_date FROM shipments WHERE order_id IN (1, 2);
