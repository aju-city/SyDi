-- =========================================================
-- IPOS_SA DATABASE SETUP
-- Creates the IPOS_SA database and application tables
-- =========================================================

CREATE DATABASE IF NOT EXISTS `ipos_sa`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE `ipos_sa`;

-- =========================================================
-- COMMERCIAL APPLICATIONS
-- Stores commercial applications submitted from IPOS-PU
-- =========================================================
CREATE TABLE IF NOT EXISTS `commercial_applications` (
                                                         `application_id` INT AUTO_INCREMENT PRIMARY KEY,
                                                         `company_name` VARCHAR(100) NOT NULL,
    `reg_number` VARCHAR(50),
    `director_details` TEXT,
    `business_type` VARCHAR(100),
    `address` TEXT,
    `email` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(30),
    `status` ENUM('submitted','under_review','approved','rejected') NOT NULL DEFAULT 'submitted',
    `submitted_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `reviewed_by` INT NULL,
    `notes` TEXT
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;