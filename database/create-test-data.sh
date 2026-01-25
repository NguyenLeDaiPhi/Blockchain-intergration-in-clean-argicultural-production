#!/bin/bash

# Script để tạo dữ liệu test: Product APPROVED và Order CONFIRMED
# Để test: Retailer xem marketplace, Retailer xem order, Farm-manager xem order, Shipping-manager nhận shipment

echo "=========================================="
echo "📦 Tạo dữ liệu test: Marketplace & Orders"
echo "=========================================="
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Trading Order Database
echo "1️⃣ Tạo dữ liệu trong trading-order-db..."

docker-compose exec -T trading-order-db mysql -uroot -proot bicap_order_db <<'EOF'
USE bicap_order_db;
-- 1. Tạo Farm Manager
INSERT INTO farm_manager (id, username, email, role, farm_id) 
VALUES (1, 'farmmanager1', 'farm@manager.com', 'ROLE_FARMMANAGER', 1)
ON DUPLICATE KEY UPDATE username=username, email='farm@manager.com', farm_id=1;

-- 2. Tạo Marketplace Product APPROVED
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

-- 3. Tạo Product thứ 2
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

-- 4. Tạo Order CONFIRMED
INSERT INTO orders (
    id, buyer_id, buyer_email, shipping_address, total_amount, status, created_at
) VALUES (
    1,
    2,
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

-- 5. Tạo Order Items
INSERT INTO order_items (
    id, order_id, product_id, quantity, unit_price
) VALUES (
    1,
    1,
    1,
    10,
    50000.00
)
ON DUPLICATE KEY UPDATE 
    quantity=10,
    unit_price=50000.00;

-- 6. Tạo Order thứ 2
INSERT INTO orders (
    id, buyer_id, buyer_email, shipping_address, total_amount, status, created_at
) VALUES (
    2,
    2,
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
    2,
    10,
    30000.00
)
ON DUPLICATE KEY UPDATE 
    quantity=10,
    unit_price=30000.00;
EOF

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Dữ liệu trading-order-db đã được tạo!${NC}"
else
    echo -e "${YELLOW}⚠️ Có lỗi khi tạo dữ liệu trading-order-db${NC}"
fi

echo ""
echo "2️⃣ Tạo dữ liệu trong shipping-db..."

docker-compose exec -T shipping-db mysql -uroot -proot shipping_db <<'EOF'
USE shipping_db;
-- 8. Tạo Shipment cho order 1
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
EOF

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Dữ liệu shipping-db đã được tạo!${NC}"
else
    echo -e "${YELLOW}⚠️ Có lỗi khi tạo dữ liệu shipping-db${NC}"
fi

echo ""
echo "=========================================="
echo "✅ Hoàn thành!"
echo "=========================================="
echo ""
echo "📋 Dữ liệu đã tạo:"
echo ""
echo "✅ Products (APPROVED) - Hiển thị trên marketplace:"
echo "   - Product 1: Cà chua sạch Đà Lạt - 50,000 VND/kg"
echo "   - Product 2: Rau cải xanh - 30,000 VND/kg"
echo ""
echo "✅ Orders (CONFIRMED) - Để test:"
echo "   - Order 1: 10kg Cà chua - 500,000 VND - retailer@test.com"
echo "   - Order 2: 10kg Rau cải - 300,000 VND - retailer@test.com"
echo ""
echo "✅ Shipments (PENDING) - Để test shipping-manager:"
echo "   - Shipment 1: Order ID 1 - PENDING"
echo "   - Shipment 2: Order ID 2 - PENDING"
echo ""
echo "🧪 Cách test:"
echo "1. Retailer (http://localhost:3000):"
echo "   - Đăng nhập: retailer@test.com"
echo "   - Xem Marketplace: Sẽ thấy 2 products APPROVED"
echo "   - Xem My Orders: Sẽ thấy 2 orders CONFIRMED"
echo ""
echo "2. Farm-manager (http://localhost:3002):"
echo "   - Đăng nhập: farm@manager.com"
echo "   - Xem Orders: Sẽ thấy 2 orders CONFIRMED"
echo ""
echo "3. Shipping-manager (http://localhost:3003):"
echo "   - Đăng nhập với tài khoản shipping-manager"
echo "   - Xem Orders: Sẽ thấy 2 shipments PENDING"
echo ""
