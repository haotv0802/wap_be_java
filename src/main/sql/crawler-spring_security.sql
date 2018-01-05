USE `crawler_db_spring_security`;
--
-- Table structure for table `user_role`
--
DROP TABLE IF EXISTS `crwlr_user_roles`;
CREATE TABLE `crwlr_user_roles` (
  `id`        BIGINT      NOT NULL,
  `role_name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_role_id_unique` (`id`),
  UNIQUE KEY `user_role_role_name_unique` (`role_name`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

INSERT INTO `crwlr_user_roles` VALUES (1, 'ADMIN'), (2, 'STAFF'), (3, 'CUSTOMER'), (4, 'USER');

--
-- Table structure for table `user_table`
--
DROP TABLE IF EXISTS `crwlr_users`;
CREATE TABLE `crwlr_users` (
  `id`        BIGINT      NOT NULL,
  `user_name` VARCHAR(45) NOT NULL,
  `password`  VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `crwlr_users_id_unique` (`id`),
  UNIQUE KEY `crwlr_users_user_name_unique` (`user_name`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


INSERT INTO `crwlr_users`
VALUES
  (1, 'admin', 'admin'), (2, 'haho', 'hoanhhao'), (3, 'hao', 'hiep'), (4, 'hiep', 'hiep'),
  (6, 'admin1', 'admin'), (7, 'admin2', 'admin'), (8, 'admin3', 'admin'),
  (9, 'admin4', 'admin'), (12, 'haho1', 'hoanhhao'), (13, 'haho13', 'hoanhhao'),
  (14, 'haho14', 'hoanhhao'), (15, 'haho15', 'hoanhhao'), (16, 'haho16', 'hoanhhao'),
  (17, 'haho17', 'hoanhhao'), (18, 'haho18', 'hoanhhao'), (19, 'haho19', 'hoanhhao'),
  (20, 'haho20', 'hoanhhao'), (21, 'haho21', 'hoanhhao'), (22, 'haho22', 'hoanhhao'),
  (23, 'haho23', 'hoanhhao'), (24, 'haho24', 'hoanhhao'), (25, 'haho25', 'hoanhhao'),
  (26, 'haho26', 'hoanhhao'), (27, 'haho27', 'hoanhhao'), (28, 'haho28', 'hoanhhao'),
  (29, 'haho29', 'hoanhhao'), (30, 'haho30', 'hoanhhao'), (31, 'haho31', 'hoanhhao'),
  (32, 'haho32', 'hoanhhao'), (33, 'haho33', 'hoanhhao'), (34, 'haho34', 'hoanhhao'),
  (36, 'haho36', 'hoanhhao'), (37, 'haho37', 'hoanhhao'), (38, 'haho38', 'hoanhhao'),
  (39, 'customer', 'customer'), (40, 'staff', 'staff'),
  (41, 'haho41', 'hoanhhao'), (42, 'haho42', 'hoanhhao'), (43, 'haho43', 'hoanhhao'),
  (44, 'haho44', 'hoanhhao'), (45, 'haho45', 'hoanhhao'), (46, 'haho46', 'hoanhhao'),
  (47, 'haho47', 'hoanhhao'), (48, 'haho48', 'hoanhhao'), (5, 'haho5', 'hoanhhao'),
  (10, 'haho10', 'hoanhhao'), (11, 'haho11', 'hoanhhao'), (49, 'haho49', 'hoanhhao'),
  (50, 'haho50', 'hoanhhao'), (51, 'haho52', 'hoanhhao'), (53, 'haho53', 'hoanhhao'),
  (54, 'haho55', 'hoanhhao'), (56, 'haho56', 'hoanhhao'), (57, 'haho57', 'hoanhhao'),
  (58, 'haho58', 'hoanhhao'), (59, 'haho59', 'hoanhhao'), (60, 'haho60', 'hoanhhao'),
  (61, 'haho61', 'hoanhhao'), (62, 'haho62', 'hoanhhao'), (63, 'haho63', 'hoanhhao')
;

--
-- Table structure for table `user_role_details`
--
DROP TABLE IF EXISTS `crwlr_user_role_details`;
CREATE TABLE `crwlr_user_role_details` (
  `id`      BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_role_unique` (`user_id`, `role_id`),
  UNIQUE KEY `user_role_user_id_unique` (`user_id`), #  A user has just ONLY 1 role.
  KEY `role_id` (`role_id`),
  CONSTRAINT `crwlr_user_role_details_role_id` FOREIGN KEY (`role_id`) REFERENCES `crwlr_user_roles` (`id`),
  CONSTRAINT `crwlr_user_role_details_user_id` FOREIGN KEY (`user_id`) REFERENCES `crwlr_users` (`id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 11
  DEFAULT CHARSET = utf8;

# INSERT INTO `user_role_details` VALUES
#   (1, 1, 1), (10, 2, 1), (2, 2, 2), (3, 2, 3), (4, 2, 4), (9, 3, 1), (6, 4, 1),
#   (7, 39, 3), (8, 40, 2)
# ;

INSERT INTO `crwlr_user_role_details` VALUES
  (1, 1, 1), (2, 2, 1), (3, 3, 1), (4, 4, 1),
  (5, 39, 3), (6, 40, 2), (7, 5, 2), (8, 6, 2)
#   (9, 7, 3), (10, 8, 2), (11, 9, 2), (12, 10, 2),
#   (13, 11, 3), (14, 12, 2), (15, 13, 2), (16, 14, 2),
#   (17, 15, 3), (18, 41, 2), (19, 16, 2), (20, 17, 2),
#   (21, 18, 3), (22, 19, 2), (23, 42, 2), (24, 20, 2),
#   (25, 21, 3), (26, 22, 2), (27, 23, 2), (28, 24, 2),
#   (29, 25, 3), (30, 26, 2), (31, 27, 2), (32, 28, 2),
#   (33, 29, 3), (34, 30, 2), (35, 31, 2), (36, 32, 2),
#   (37, 33, 3), (38, 34, 2), (39, 35, 2), (40, 36, 2),
#   (41, 37, 3), (42, 38, 2), (43, 39, 2), (44, 40, 2)
;


--
-- Table structure for table `error_tracking`
--
DROP TABLE IF EXISTS `crwlr_error_tracking`;
CREATE TABLE `crwlr_error_tracking` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT,
  `error_message` VARCHAR(100) NOT NULL,
  `stack_trace`   TEXT         NOT NULL,
  `user`          VARCHAR(50),
  `error_date`    DATETIME     NOT NULL DEFAULT now(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `crwlr_error_tracking_id_unique` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `auth_token`
--
DROP TABLE IF EXISTS `crwlr_auth_token`;
CREATE TABLE `crwlr_auth_token` (
  `id`          BIGINT AUTO_INCREMENT,
  `token_type`  VARCHAR(45) NOT NULL,
  `auth_object` BLOB        NOT NULL,
  `exp_date`    DATETIME    NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `crwlr_auth_token_id_unique` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;