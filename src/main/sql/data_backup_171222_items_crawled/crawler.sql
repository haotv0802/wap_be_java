DROP DATABASE IF EXISTS `crawler_db`;
CREATE DATABASE IF NOT EXISTS `crawler_db`;
USE `crawler_db`;

--
-- Table structure for table `crwlr_categories`
--
DROP TABLE IF EXISTS `crwlr_categories`;
CREATE TABLE `crwlr_categories` (
  `id`      BIGINT   AUTO_INCREMENT,
  `name`    NVARCHAR(200) NULL,
  `url`     NVARCHAR(200) NULL,
  `created` DATETIME DEFAULT NOW(),
  `updated` DATETIME      NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `crwlr_categories_id` (`id`),
  UNIQUE KEY `crwlr_categories_url` (`url`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `crwlr_categories`
--
DROP TABLE IF EXISTS `crwlr_tracking`;
CREATE TABLE `crwlr_tracking` (
  `id`             BIGINT   AUTO_INCREMENT,
  `category_id`    BIGINT   NOT NULL,
  `crawling_start` DATETIME NOT NULL,
  `crawling_end`   DATETIME NOT NULL,
  `created`        DATETIME DEFAULT NOW(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `crwlr_tracking_id` (`id`),
  CONSTRAINT `crwlr_tracking_category_id` FOREIGN KEY (`category_id`) REFERENCES `crwlr_categories` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `crwlr_items`
--
DROP TABLE IF EXISTS `crwlr_items`;
CREATE TABLE `crwlr_items` (
  `id`             BIGINT   AUTO_INCREMENT,
  `name`           NVARCHAR(200) NULL,
  `address`        VARCHAR(300)  NULL,
  `description`    TEXT          NOT NULL,
  `contactName`    VARCHAR(200)  NULL,
  `contactNumber`  VARCHAR(45)   NULL,
  `contactEmail`   VARCHAR(100)  NULL,
  `acreage`        VARCHAR(100)  NULL,
  `price`          VARCHAR(100)  NULL,
  `location`       VARCHAR(100)  NULL,
  `publish_date`   DATE          NULL,
  `end_date`       DATE          NULL,
  `url`            TEXT          NULL,
  `category_id`    BIGINT        NOT NULL,
  `crawling_start` DATETIME      NOT NULL,
  `crawling_end`   DATETIME      NOT NULL,
  `created`        DATETIME DEFAULT NOW(),
  `updated`        DATETIME      NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `crwlr_items` (`id`),
  CONSTRAINT `crwlr_items_category_url` FOREIGN KEY (`category_id`) REFERENCES `crwlr_categories` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `crwlr_vendors`
--
DROP TABLE IF EXISTS `crwlr_vendors`;
CREATE TABLE `crwlr_vendors` (
  `id`           BIGINT   AUTO_INCREMENT,
  `name`         VARCHAR(200)  NULL,
  `location`     VARCHAR(45)   NULL,
  `positive`     INT           NULL,
  `neutral`      INT           NULL,
  `negative`     INT           NULL,
  `link`         VARCHAR(100)  NULL,
  `timeOnLazada` TINYINT       NULL,
  `rating`       DECIMAL(5, 2) NULL,
  `mainCategory` VARCHAR(45)   NULL,
  `size`         TINYINT       NULL,
  `shipOnTime`   DECIMAL(5, 2) NULL,
  `created`      DATETIME DEFAULT NOW(),
  `updated`      DATETIME      NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `crwlr_vendors_id` (`id`),
  UNIQUE KEY `crwlr_vendors_name` (`name`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `crwlr_products`
--
DROP TABLE IF EXISTS `crwlr_products`;
CREATE TABLE `crwlr_products` (
  `id`              BIGINT   AUTO_INCREMENT,
  `name`            VARCHAR(300)   NOT NULL,
  `category`        VARCHAR(45)    NOT NULL,
  `vendor_name`     VARCHAR(45)    NOT NULL,
  `link`            TEXT           NULL,
  `price`           DECIMAL(13, 4) NULL,
  `discountPrice`   DECIMAL(13, 4) NULL,
  `currency`        VARCHAR(4)     NULL,
  `discountPercent` DECIMAL(9, 4)  NULL,
  `imageURL`        TEXT           NULL,
  `created`         DATETIME DEFAULT NOW(),
  `updated`         DATETIME       NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `crwlr_products` (`id`),
  CONSTRAINT `crwlr_products_vendor_name` FOREIGN KEY (`vendor_name`) REFERENCES `crwlr_vendors` (`name`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;