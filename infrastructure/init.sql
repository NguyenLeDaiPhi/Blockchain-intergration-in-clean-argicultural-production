-- 1. Database cho Farm Production Service (SỬA TÊN CHO KHỚP)
CREATE DATABASE IF NOT EXISTS farm_management_db;

-- 2. Database cho Blockchain Adapter Service
CREATE DATABASE IF NOT EXISTS bicap_blockchain_db;

-- 3. Database cho Shipping Manager Service
CREATE DATABASE IF NOT EXISTS shipping_db;
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%';
FLUSH PRIVILEGES;