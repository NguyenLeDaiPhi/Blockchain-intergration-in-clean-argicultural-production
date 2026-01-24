-- Grant privileges for root from any host
-- This script runs first to ensure root can connect from Docker network

CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY '0862264719Phi';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;
