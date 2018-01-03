DROP DATABASE IF EXISTS `crawler_db_180104`;
CREATE DATABASE IF NOT EXISTS `crawler_db_180104`
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
USE `crawler_db_180104`;

--
-- Table structure for table `crwlr_crawling_tracking`
--
DROP TABLE IF EXISTS `crwlr_crawling_tracking`;
CREATE TABLE `crwlr_crawling_tracking` (
  `id`            BIGINT   AUTO_INCREMENT,
  `name`          VARCHAR(200)
                  CHARACTER SET utf8mb4
                  COLLATE utf8mb4_unicode_ci NULL,
  `url`           NVARCHAR(500)              NULL,
  `items_count`   INT                        NOT NULL,
  `items_crawled` INT                        NOT NULL,
  `items_added`   INT                        NOT NULL,
  `created_at`    DATETIME DEFAULT NOW(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `crwlr_crawling_tracking_id` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `crwlr_locations`
--
DROP TABLE IF EXISTS `crwlr_locations`;
CREATE TABLE `crwlr_locations` (
  `id`       BIGINT AUTO_INCREMENT,
  `city`     NVARCHAR(100),
  `district` NVARCHAR(100),
  PRIMARY KEY (`id`),
  UNIQUE KEY `crwlr_locations_city_district` (`city`, `district`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `crwlr_categories`
--
DROP TABLE IF EXISTS `crwlr_categories`;
CREATE TABLE `crwlr_categories` (
  `id`            BIGINT   AUTO_INCREMENT,
  `name`          VARCHAR(200)
                  CHARACTER SET utf8mb4
                  COLLATE utf8mb4_unicode_ci           NULL,
  `url`           NVARCHAR(500)                        NULL,
  `source`        NVARCHAR(100)                        NULL,
  `type`          VARCHAR(100) DEFAULT 'Selling'       NOT NULL, # Selling house, buying house, selling apartment, buying apartment
  `property_type` VARCHAR(100) DEFAULT 'House'         NOT NULL,
  `location_id`   BIGINT                               NOT NULL,
  `created_at`    DATETIME DEFAULT NOW(),
  #   `updated`      DATETIME      NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `crwlr_categories_id` (`id`),
  UNIQUE KEY `crwlr_categories_url` (`url`),
  CONSTRAINT `crwlr_categories_location_id` FOREIGN KEY (`location_id`) REFERENCES `crwlr_locations` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `crwlr_items`
--
DROP TABLE IF EXISTS `crwlr_items`;
CREATE TABLE `crwlr_items` (
  `id`             BIGINT   AUTO_INCREMENT,
  `name`           VARCHAR(200)
                   CHARACTER SET utf8mb4
                   COLLATE utf8mb4_unicode_ci NULL,
  `address`        VARCHAR(300)
                   CHARACTER SET utf8mb4
                   COLLATE utf8mb4_unicode_ci NULL,
  `description`    LONGTEXT CHARACTER SET utf8mb4
                   COLLATE utf8mb4_unicode_ci NULL,
  `contact_name`   VARCHAR(200)               NULL,
  `contact_number` VARCHAR(45)                NULL,
  `contact_email`  VARCHAR(100)               NULL,
  `acreage`        DECIMAL(13, 2)             NULL,
  `price`          DECIMAL(13, 2)             NULL,
  `district`       VARCHAR(50)                NULL,
  `city`           VARCHAR(50)                NULL,
  `publish_date`   DATE                       NULL,
  `end_date`       DATE                       NULL,
  `url`            NVARCHAR(500)              NULL,
  `location_id`    BIGINT                     NULL,
  #   `source`
  #   `crawling_start` DATETIME                   NOT NULL,
  #   `crawling_end`   DATETIME                   NOT NULL,
  `created_at`     DATETIME DEFAULT NOW(),
  `updated_at`     DATETIME                   NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `crwlr_items_id` (`id`),
  UNIQUE KEY `crwlr_items_url` (`url`),
  CONSTRAINT `crwlr_items_location_id` FOREIGN KEY (`location_id`) REFERENCES `crwlr_locations` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `crwlr_categories_items_details`
--
DROP TABLE IF EXISTS `crwlr_categories_items_details`;
CREATE TABLE `crwlr_categories_items_details` (
  `id`          BIGINT   AUTO_INCREMENT,
  `category_id` BIGINT NOT NULL,
  `item_id`     BIGINT NOT NULL,
  `created`     DATETIME DEFAULT NOW(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `crwlr_categories_id` (`category_id`, `item_id`),
  CONSTRAINT `crwlr_categories_items_details_category_id` FOREIGN KEY (`category_id`) REFERENCES `crwlr_categories` (`id`),
  CONSTRAINT `crwlr_categories_items_details_item_id` FOREIGN KEY (`item_id`) REFERENCES `crwlr_items` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `crwlr_contacts`
--
DROP TABLE IF EXISTS `crwlr_contacts`;
CREATE TABLE `crwlr_contacts` (
  `id`                    BIGINT   AUTO_INCREMENT,
  `name`                  NVARCHAR(100),
  `phone`                 NVARCHAR(15),
  `email`                 NVARCHAR(150),
  `type`                  NVARCHAR(20), #SALE or OWNER
  `latest_item_posted_at` DATE     NOT NULL, # User will be notified if this contact is in the list again just in short period of time.
  `created_at`            DATETIME DEFAULT now(),
  `updated_at`            DATETIME NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `crwlr_contacts_backup`
--
DROP TABLE IF EXISTS `crwlr_contacts_backup`;
CREATE TABLE `crwlr_contacts_backup` (
  `id`                    BIGINT AUTO_INCREMENT,
  `name`                  NVARCHAR(100),
  `phone`                 NVARCHAR(15),
  `email`                 NVARCHAR(150),
  `type`                  NVARCHAR(20), #SALE or OWNER
  `latest_item_posted_at` DATE     NOT NULL,
  `contact_id`            BIGINT   NOT NULL,
  `created_at`            DATETIME NOT NULL, # the same with updated_at in contacts table.
  PRIMARY KEY (`id`),
  CONSTRAINT `crwlr_contacts_backup_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `crwlr_contacts` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `crwlr_customers`
--
DROP TABLE IF EXISTS `crwlr_customers`;
CREATE TABLE `crwlr_customers` (
  `id`               BIGINT   AUTO_INCREMENT,
  `name`             NVARCHAR(100),
  `phone`            NVARCHAR(15),
  `email`            NVARCHAR(150),
  `latest_export_at` DATETIME NOT NULL,
  `contact_id`       BIGINT   NOT NULL,
  `created_at`       DATETIME DEFAULT now(),
  `updated_at`       DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `crwlr_customers_email` (`email`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `crwlr_exports_tracking`
--
DROP TABLE IF EXISTS `crwlr_exports_tracking`;
CREATE TABLE `crwlr_exports_tracking` (
  `id`          BIGINT   AUTO_INCREMENT,
  `customer_id` BIGINT NOT NULL,
  `contact_id`  BIGINT NOT NULL,
  `exported_at` DATETIME DEFAULT now(),
  PRIMARY KEY (`id`),
  CONSTRAINT `crwlr_exports_tracking_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `crwlr_customers` (`id`),
  CONSTRAINT `crwlr_exports_tracking_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `crwlr_contacts` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;