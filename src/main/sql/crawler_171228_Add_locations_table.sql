DROP DATABASE IF EXISTS `crawler_db4`;
CREATE DATABASE IF NOT EXISTS `crawler_db4`;
USE `crawler_db4`;

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
  `created`       DATETIME DEFAULT NOW(),
  #   `updated`      DATETIME      NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `crwlr_crawling_tracking_id` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `crwlr_categories`
--
DROP TABLE IF EXISTS `crwlr_categories`;
CREATE TABLE `crwlr_categories` (
  `id`           BIGINT   AUTO_INCREMENT,
  `name`         VARCHAR(200)
                 CHARACTER SET utf8                   NULL,
  `url`          NVARCHAR(500)                        NULL,
  `source`       NVARCHAR(100)                        NULL,
  #   `category_name` NVARCHAR(100)                        NULL,
  `type`         VARCHAR(100) DEFAULT 'Selling'       NOT NULL, # Selling house, buying house, selling apartment, buying apartment
  `propertyType` VARCHAR(100) DEFAULT 'House'         NOT NULL,
  `city`         VARCHAR(100) DEFAULT 'Ho Chi Minh'   NOT NULL,
  `district`     VARCHAR(100) DEFAULT 'ALL'           NOT NULL,
  `created`      DATETIME DEFAULT NOW(),
  #   `updated`      DATETIME      NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `crwlr_categories_id` (`id`),
  UNIQUE KEY `crwlr_categories_url` (`url`)
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
-- Table structure for table `crwlr_items`
--
DROP TABLE IF EXISTS `crwlr_items`;
CREATE TABLE `crwlr_items` (
  `id`             BIGINT   AUTO_INCREMENT,
  `name`           VARCHAR(200)
                   CHARACTER SET utf8mb4
                   COLLATE utf8mb4_unicode_ci NULL,
  `address`        VARCHAR(300)               NULL,
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
  `created`        DATETIME DEFAULT NOW(),
  `updated`        DATETIME                   NULL,
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
# -- Table structure for table `crwlr_categories`
# --
# DROP TABLE IF EXISTS `crwlr_crawling_tracking_items_details`;
# CREATE TABLE `crwlr_crawling_tracking_items_details` (
#   `id`                   BIGINT   AUTO_INCREMENT,
#   `crawling_tracking_id` BIGINT NOT NULL,
#   `item_id`              BIGINT NOT NULL,
#   `created`              DATETIME DEFAULT NOW(),
#   PRIMARY KEY (`id`),
#   UNIQUE KEY `crwlr_crawling_tracking_items_details_id` (`crawling_tracking_id`, `item_id`),
#   CONSTRAINT `crwlr_crawling_tracking_items_details_crawling_tracking_id` FOREIGN KEY (`crawling_tracking_id`) REFERENCES `crwlr_crawling_tracking` (`id`),
#   CONSTRAINT `crwlr_crawling_tracking_items_details_item_id` FOREIGN KEY (`item_id`) REFERENCES `crwlr_items` (`id`)
# )
#   ENGINE = InnoDB
#   DEFAULT CHARSET = utf8;