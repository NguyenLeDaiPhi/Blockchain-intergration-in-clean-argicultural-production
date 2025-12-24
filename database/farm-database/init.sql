-- Tạo bảng Mùa vụ
DROP TABLE IF EXISTS `farming_seasons`;
CREATE TABLE `farming_seasons` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `description` TEXT,
    `farm_id` BIGINT NOT NULL,
    `product_type` VARCHAR(255),
    `area` DOUBLE,
    `start_date` DATETIME,
    `expected_harvest_date` DATETIME,
    `actual_harvest_date` DATETIME,
    `status` VARCHAR(50),
    `blockchain_tx_id` VARCHAR(66),
    `data_hash` VARCHAR(64),
    `created_at` DATETIME,
    `updated_at` DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tạo bảng Quy trình canh tác
DROP TABLE IF EXISTS `farming_processes`;
CREATE TABLE `farming_processes` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `season_id` BIGINT NOT NULL,
    `activity_type` VARCHAR(100),
    `description` TEXT,
    `performed_date` DATETIME,
    `blockchain_tx_id` VARCHAR(66),
    `data_hash` VARCHAR(64),
    `sync_status` VARCHAR(50),
    FOREIGN KEY (`season_id`) REFERENCES `farming_seasons`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;