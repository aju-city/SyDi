-- =========================================================
-- IPOS_CA DATABASE SETUP
-- Creates the IPOS_CA database and stock tables
-- =========================================================

CREATE DATABASE IF NOT EXISTS `ipos_ca`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE `ipos_ca`;

-- =========================================================
-- STOCK ITEMS
-- Stores all catalogue items available in the CA subsystem
-- =========================================================
CREATE TABLE `stock_items` (
                               `item_id` varchar(50) NOT NULL,
                               `name` varchar(100) NOT NULL,
                               `description` varchar(255) DEFAULT NULL,
                               `package_type` varchar(50) DEFAULT NULL,
                               `unit` varchar(20) DEFAULT NULL,
                               `units_per_pack` int DEFAULT NULL,
                               `price` decimal(10,2) NOT NULL,
                               `quantity` int DEFAULT '0',
                               `stock_limit` int DEFAULT '10',
                               PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;