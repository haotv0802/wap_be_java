DROP DATABASE IF EXISTS `crawler_db_180111`;
CREATE DATABASE IF NOT EXISTS `crawler_db_180111`
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
USE `crawler_db_180111`;

--
-- Table structure for table `crwlr_crawling_tracking`
--
DROP TABLE IF EXISTS `crwlr_crawling_tracking`;
CREATE TABLE `crwlr_crawling_tracking` (
  `id`            BIGINT   AUTO_INCREMENT,
  `name`          VARCHAR(200)
                  CHARACTER SET utf8mb4
                  COLLATE utf8mb4_unicode_ci NULL,
  `url`           VARCHAR(500)               NOT NULL,
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
  `city`     VARCHAR(100) NOT NULL,
  `district` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `crwlr_locations_city_district` (`city`, `district`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `crwlr_contacts`
--
DROP TABLE IF EXISTS `crwlr_contacts`;
CREATE TABLE `crwlr_contacts` (
  `id`                    BIGINT   AUTO_INCREMENT,
  `name`                  VARCHAR(100) NULL,
  `phone`                 VARCHAR(100) NULL,
  `email`                 VARCHAR(150) NULL,
  `type`                  VARCHAR(20)  NOT NULL, #SALE or OWNER
  `manual_check`          VARCHAR(20)  NULL, #SALE or OWNER
  `email_existing`        BOOLEAN      NULL, #Exiting or not
  `latest_item_posted_on` DATE         NULL, #User will be notified if this contact is in the list again just in short period of time.
  `subscribed`            BOOLEAN  DEFAULT FALSE,
  `description`           LONGTEXT CHARACTER SET utf8mb4,
  `created_at`            DATETIME DEFAULT now(),
  `updated_at`            DATETIME     NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `crwlr_contacts_name_phone_email` (`name`, `phone`, `email`)

)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `crwlr_posts`
--
DROP TABLE IF EXISTS `crwlr_posts`;
CREATE TABLE `crwlr_posts` (
  `id`             BIGINT   AUTO_INCREMENT,
  `name`           VARCHAR(200)
                   CHARACTER SET utf8mb4
                   COLLATE utf8mb4_unicode_ci           NULL,
  `address`        VARCHAR(300)
                   CHARACTER SET utf8mb4
                   COLLATE utf8mb4_unicode_ci           NULL,
  `description`    LONGTEXT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci                            NULL,
  `contact_name`   VARCHAR(200)                         NULL,
  `contact_number` VARCHAR(45)                          NULL,
  `contact_email`  VARCHAR(100)                         NULL,
  `acreage`        DECIMAL(13, 2)                       NULL,
  `price`          DECIMAL(13, 2)                       NULL,
  `publish_date`   DATE                                 NULL,
  `end_date`       DATE                                 NULL,
  `url`            VARCHAR(500)                         NOT NULL,
  `location_id`    BIGINT                               NOT NULL,
  `source`         VARCHAR(100)                         NULL,
  `type`           VARCHAR(100) DEFAULT 'Selling'       NULL, # Selling house, buying house, selling apartment, buying apartment
  `property_type`  VARCHAR(100) DEFAULT 'House'         NULL,
  #   `crawling_start` DATETIME                   NOT NULL,
  #   `crawling_end`   DATETIME                   NOT NULL,
  `contact_id`     BIGINT                               NOT NULL,
  `created_at`     DATETIME DEFAULT NOW(),
  `updated_at`     DATETIME                             NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `crwlr_posts_id` (`id`),
  UNIQUE KEY `crwlr_posts_url` (`url`),
  CONSTRAINT `crwlr_posts_location_id` FOREIGN KEY (`location_id`) REFERENCES `crwlr_locations` (`id`),
  CONSTRAINT `crwlr_posts_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `crwlr_contacts` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `crwlr_customers`
--
DROP TABLE IF EXISTS `crwlr_customers`;
CREATE TABLE `crwlr_customers` (
  `id`               BIGINT   AUTO_INCREMENT,
  `name`             VARCHAR(100),
  `phone`            VARCHAR(100),
  `email`            VARCHAR(150),
  `latest_export_at` DATETIME NULL,
  #   `contact_id`       BIGINT   NULL,
  `created_at`       DATETIME DEFAULT now(),
  `updated_at`       DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `crwlr_customers_email` (`email`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `crwlr_customers`
--
DROP TABLE IF EXISTS `crwlr_sent_emails_tracking`;
CREATE TABLE `crwlr_sent_emails_tracking` (
  `id`         BIGINT   AUTO_INCREMENT,
  `from_email` VARCHAR(100),
  `title`      VARCHAR(200),
  `to_email`   VARCHAR(100),
  `content`    LONGTEXT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci NULL,
  `created_at` DATETIME DEFAULT now(),
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


INSERT INTO crwlr_customers (`name`, `phone`, `email`, `latest_export_at`)
VALUES ('Phuc', '01235379247', 'hongphuc.vumai@gmail.com', now());


INSERT INTO crwlr_customers (`name`, `phone`, `email`, `latest_export_at`)
VALUES ('Thanh', '01269679581', 'nguyenphuoc.thanh110795@gmail.com', now());

INSERT INTO crwlr_customers (name, phone, email) VALUES ('Duc Nguyen Doan', '0941179119', 'nguyendoanduc.up@gmail.com');
--
-- Table structure for table `crwlr_exports_tracking`
--
DROP TABLE IF EXISTS `crwlr_exports_tracking`;
CREATE TABLE `crwlr_exports_tracking` (
  `id`          BIGINT   AUTO_INCREMENT,
  `customer_id` BIGINT NOT NULL,
  `contact_id`  BIGINT NOT NULL,
  `file_name`   VARCHAR(200),
  `exported_at` DATETIME DEFAULT now(),
  `criterion`   TEXT   NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `crwlr_exports_tracking_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `crwlr_customers` (`id`),
  CONSTRAINT `crwlr_exports_tracking_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `crwlr_contacts` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


--
-- Table structure for table `error_tracking`
--
DROP TABLE IF EXISTS `error_tracking`;
CREATE TABLE `error_tracking` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT,
  `error_message` VARCHAR(100) NOT NULL,
  `stack_trace`   TEXT         NOT NULL,
  `user`          VARCHAR(50),
  `error_date`    DATETIME     NOT NULL DEFAULT now(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `error_tracking_id_unique` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;