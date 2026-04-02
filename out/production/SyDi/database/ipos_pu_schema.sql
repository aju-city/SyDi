-- =========================================================
-- IPOS_PU DATABASE SETUP
-- Creates the IPOS_PU database and all main tables
-- =========================================================

CREATE DATABASE IF NOT EXISTS `ipos_pu`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE `ipos_pu`;

-- =========================================================
-- ADMIN USERS
-- Stores PU admin login details and roles
-- =========================================================
CREATE TABLE `AdminUsers` (
                              `Username` varchar(100) NOT NULL,
                              `Password` varchar(255) NOT NULL,
                              `Role` varchar(50) NOT NULL DEFAULT 'PU-Admin',
                              PRIMARY KEY (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =========================================================
-- NON-COMMERCIAL MEMBERS
-- Stores registered member accounts for PU users
-- =========================================================
CREATE TABLE `NonCommercialMember` (
                                       `MemberID` int NOT NULL AUTO_INCREMENT,
                                       `Email` varchar(255) NOT NULL,
                                       `Password` varchar(255) NOT NULL,
                                       `MustChangePassword` tinyint(1) NOT NULL DEFAULT '1',
                                       `TotalOrders` int NOT NULL DEFAULT '0',
                                       `CreatedAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                       `MemberAccountNo` varchar(20) DEFAULT NULL,
                                       PRIMARY KEY (`MemberID`),
                                       UNIQUE KEY `Email` (`Email`),
                                       UNIQUE KEY `MemberAccountNo` (`MemberAccountNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =========================================================
-- PROMOTION CAMPAIGNS
-- Stores advertising and promotion campaigns created by PU admins
-- =========================================================
CREATE TABLE `PromotionCampaign` (
                                     `campaign_id` int NOT NULL AUTO_INCREMENT,
                                     `campaign_name` varchar(150) NOT NULL,
                                     `start_datetime` datetime NOT NULL,
                                     `end_datetime` datetime NOT NULL,
                                     `status` enum('SCHEDULED','ACTIVE','ENDED','TERMINATED') NOT NULL DEFAULT 'SCHEDULED',
                                     `created_by` varchar(100) NOT NULL,
                                     `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`campaign_id`),
                                     KEY `idx_promotion_dates` (`start_datetime`,`end_datetime`),
                                     KEY `fk_promotion_created_by` (`created_by`),
                                     CONSTRAINT `fk_promotion_created_by`
                                         FOREIGN KEY (`created_by`) REFERENCES `AdminUsers` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =========================================================
-- PROMOTION CAMPAIGN ITEMS
-- Stores which CA stock items are included in each campaign
-- =========================================================
CREATE TABLE `PromotionCampaignItems` (
                                          `campaign_item_id` int NOT NULL AUTO_INCREMENT,
                                          `campaign_id` int NOT NULL,
                                          `product_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                          `discount_rate` decimal(5,2) NOT NULL,
                                          PRIMARY KEY (`campaign_item_id`),
                                          UNIQUE KEY `uq_campaign_product` (`campaign_id`,`product_id`),
                                          KEY `idx_promoitems_product` (`product_id`),
                                          CONSTRAINT `fk_promoitems_campaign`
                                              FOREIGN KEY (`campaign_id`) REFERENCES `PromotionCampaign` (`campaign_id`) ON DELETE CASCADE,
                                          CONSTRAINT `fk_promoitems_product`
                                              FOREIGN KEY (`product_id`) REFERENCES `ipos_ca`.`stock_items` (`item_id`),
                                          CONSTRAINT `chk_discount_rate`
                                              CHECK ((`discount_rate` >= 0) AND (`discount_rate` <= 100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =========================================================
-- SHOPPING CART
-- Stores one active cart per member or guest
-- =========================================================
CREATE TABLE `ShoppingCart` (
                                `cart_id` int NOT NULL AUTO_INCREMENT,
                                `customer_type` enum('MEMBER','GUEST') NOT NULL DEFAULT 'MEMBER',
                                `member_email` varchar(255) DEFAULT NULL,
                                `guest_token` varchar(100) DEFAULT NULL,
                                `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                PRIMARY KEY (`cart_id`),
                                UNIQUE KEY `uq_shoppingcart_member` (`member_email`),
                                UNIQUE KEY `uq_shoppingcart_guest` (`guest_token`),
                                CONSTRAINT `fk_shoppingcart_member`
                                    FOREIGN KEY (`member_email`) REFERENCES `NonCommercialMember` (`Email`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =========================================================
-- SHOPPING CART ITEMS
-- Stores products currently added to a shopping cart
-- =========================================================
CREATE TABLE `ShoppingCartItems` (
                                     `cart_item_id` int NOT NULL AUTO_INCREMENT,
                                     `cart_id` int NOT NULL,
                                     `product_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                     `quantity` int NOT NULL DEFAULT '1',
                                     PRIMARY KEY (`cart_item_id`),
                                     UNIQUE KEY `uq_cart_product` (`cart_id`,`product_id`),
                                     KEY `idx_cartitems_product` (`product_id`),
                                     CONSTRAINT `fk_cartitems_cart`
                                         FOREIGN KEY (`cart_id`) REFERENCES `ShoppingCart` (`cart_id`) ON DELETE CASCADE,
                                     CONSTRAINT `fk_cartitems_product`
                                         FOREIGN KEY (`product_id`) REFERENCES `ipos_ca`.`stock_items` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =========================================================
-- ORDERS
-- Stores completed member and guest orders
-- =========================================================
CREATE TABLE `Orders` (
                          `order_id` int NOT NULL AUTO_INCREMENT,
                          `customer_type` enum('MEMBER','GUEST') NOT NULL DEFAULT 'MEMBER',
                          `member_email` varchar(255) DEFAULT NULL,
                          `customer_email` varchar(255) NOT NULL,
                          `order_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          `delivery_address` varchar(255) NOT NULL,
                          `total_amount` decimal(10,2) NOT NULL DEFAULT '0.00',
                          `status` enum('Confirmed','Preparing','Dispatched','Out for Delivery','Delivered','Cancelled') NOT NULL DEFAULT 'Confirmed',
                          PRIMARY KEY (`order_id`),
                          KEY `fk_orders_member_email` (`member_email`),
                          CONSTRAINT `fk_orders_member_email`
                              FOREIGN KEY (`member_email`) REFERENCES `NonCommercialMember` (`Email`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =========================================================
-- ORDER ITEMS
-- Stores the products included in each order
-- =========================================================
CREATE TABLE `OrderItems` (
                              `order_item_id` int NOT NULL AUTO_INCREMENT,
                              `order_id` int NOT NULL,
                              `campaign_id` int DEFAULT NULL,
                              `product_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                              `product_description` varchar(200) NOT NULL,
                              `quantity` int NOT NULL DEFAULT '1',
                              `unit_price` decimal(10,2) NOT NULL,
                              `discount_percent` decimal(5,2) DEFAULT '0.00',
                              `line_total` decimal(10,2) NOT NULL,
                              PRIMARY KEY (`order_item_id`),
                              KEY `order_id` (`order_id`),
                              KEY `product_id` (`product_id`),
                              KEY `fk_orderitems_campaign` (`campaign_id`),
                              CONSTRAINT `fk_orderitems_stock`
                                  FOREIGN KEY (`product_id`) REFERENCES `ipos_ca`.`stock_items` (`item_id`),
                              CONSTRAINT `fk_orderitems_campaign`
                                  FOREIGN KEY (`campaign_id`) REFERENCES `PromotionCampaign` (`campaign_id`),
                              CONSTRAINT `order_items_ibfk_1`
                                  FOREIGN KEY (`order_id`) REFERENCES `Orders` (`order_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =========================================================
-- PAYMENT TRANSACTIONS
-- Stores payment attempts and results for orders
-- =========================================================
CREATE TABLE `PaymentTransaction` (
                                      `payment_id` int NOT NULL AUTO_INCREMENT,
                                      `order_id` int NOT NULL,
                                      `payment_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      `amount` decimal(10,2) NOT NULL,
                                      `payment_method` enum('Credit Card','Debit Card','AmEx','Mastercard','Visa','Other') NOT NULL,
                                      `masked_card_number` varchar(30) DEFAULT NULL,
                                      `payment_status` enum('PENDING','SUCCESS','FAILED') NOT NULL DEFAULT 'PENDING',
                                      `processor_reference` varchar(100) DEFAULT NULL,
                                      `failure_reason` varchar(255) DEFAULT NULL,
                                      PRIMARY KEY (`payment_id`),
                                      KEY `idx_payment_order` (`order_id`),
                                      CONSTRAINT `fk_payment_order`
                                          FOREIGN KEY (`order_id`) REFERENCES `Orders` (`order_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =========================================================
-- EMAIL LOG
-- Stores sent emails and failed email attempts
-- =========================================================
CREATE TABLE `EmailLog` (
                            `email_id` int NOT NULL AUTO_INCREMENT,
                            `order_id` int DEFAULT NULL,
                            `recipient_email` varchar(100) NOT NULL,
                            `email_type` enum('ORDER_CONFIRMATION','TRACKING_LINK','SA_NOTIFICATION','OTHER') NOT NULL,
                            `subject` varchar(200) NOT NULL,
                            `body` text,
                            `sent_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `send_status` enum('PENDING','SENT','FAILED') NOT NULL DEFAULT 'PENDING',
                            `failure_reason` varchar(255) DEFAULT NULL,
                            PRIMARY KEY (`email_id`),
                            KEY `idx_email_order` (`order_id`),
                            CONSTRAINT `fk_email_order`
                                FOREIGN KEY (`order_id`) REFERENCES `Orders` (`order_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =========================================================
-- ACTIVITY LOG
-- Stores user activity for counters, engagement, and reports
-- =========================================================
CREATE TABLE `ActivityLog` (
                               `activity_id` int NOT NULL AUTO_INCREMENT,
                               `customer_type` enum('MEMBER','GUEST') DEFAULT NULL,
                               `member_email` varchar(255) DEFAULT NULL,
                               `guest_token` varchar(100) DEFAULT NULL,
                               `campaign_id` int DEFAULT NULL,
                               `product_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                               `order_id` int DEFAULT NULL,
                               `event_type` enum('CATALOGUE_VIEW','CAMPAIGN_VIEW','CAMPAIGN_CLICK','PRODUCT_VIEW','ADD_TO_CART','CHECKOUT','PURCHASE') NOT NULL,
                               `event_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               PRIMARY KEY (`activity_id`),
                               KEY `idx_activity_campaign` (`campaign_id`),
                               KEY `idx_activity_product` (`product_id`),
                               KEY `idx_activity_order` (`order_id`),
                               KEY `idx_activity_member` (`member_email`),
                               CONSTRAINT `fk_activity_campaign`
                                   FOREIGN KEY (`campaign_id`) REFERENCES `PromotionCampaign` (`campaign_id`) ON DELETE SET NULL,
                               CONSTRAINT `fk_activity_member`
                                   FOREIGN KEY (`member_email`) REFERENCES `NonCommercialMember` (`Email`) ON DELETE SET NULL,
                               CONSTRAINT `fk_activity_order`
                                   FOREIGN KEY (`order_id`) REFERENCES `Orders` (`order_id`) ON DELETE SET NULL,
                               CONSTRAINT `fk_activity_product`
                                   FOREIGN KEY (`product_id`) REFERENCES `ipos_ca`.`stock_items` (`item_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;