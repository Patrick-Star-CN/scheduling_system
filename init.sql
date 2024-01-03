-- the init sql for the database

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for group_tb
-- ----------------------------
DROP TABLE IF EXISTS `group_tb`;
CREATE TABLE `group_tb`
(
    `id`         int NOT NULL AUTO_INCREMENT,
    `store_id`   int          DEFAULT NULL,
    `manager_id` int          DEFAULT NULL,
    `name`       varchar(255) DEFAULT NULL,
    `type`       char(16)     DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for profession
-- ----------------------------
DROP TABLE IF EXISTS `profession`;
CREATE TABLE `profession`
(
    `id`         int NOT NULL AUTO_INCREMENT,
    `store_id`   int      DEFAULT NULL,
    `manager_id` int      DEFAULT NULL,
    `type`       char(16) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for store
-- ----------------------------
DROP TABLE IF EXISTS `store`;
CREATE TABLE `store`
(
    `store_id` int NOT NULL AUTO_INCREMENT,
    `name`     varchar(255) DEFAULT NULL,
    `address`  varchar(255) DEFAULT NULL,
    `size`     double       DEFAULT NULL,
    PRIMARY KEY (`store_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `user_id`  int NOT NULL AUTO_INCREMENT,
    `name`     varchar(255) DEFAULT NULL,
    `password` varchar(255) DEFAULT NULL,
    `type`     char(16)     DEFAULT NULL,
    `store_id` int          DEFAULT NULL,
    `group_id` int          DEFAULT NULL,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `user_pk` (`name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 19
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- ----------------------------
-- View structure for user_details_view
-- ----------------------------
DROP VIEW IF EXISTS `user_details_view`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `user_details_view` AS
select `u`.`user_id`  AS `user_id`,
       `u`.`name`     AS `name`,
       `u`.`store_id` AS `store_id`,
       `s`.`name`     AS `store_name`,
       `s`.`address`  AS `store_address`,
       `g`.`id`       AS `group_id`,
       `u2`.`name`    AS `group_manager_name`,
       `u1`.`name`    AS `manager_name`,
       `u`.`type`     AS `type`
from (((((`user` `u` join `store` `s` on ((`u`.`store_id` = `s`.`store_id`))) join `group_tb` `g`
         on ((`u`.`group_id` = `g`.`id`))) join `profession` `p`
        on (((`g`.`type` = `p`.`type`) and (`u`.`store_id` = `p`.`store_id`)))) join `user` `u1`
       on ((`u1`.`user_id` = `p`.`manager_id`))) join `user` `u2` on ((`u2`.`user_id` = `g`.`manager_id`)));

SET FOREIGN_KEY_CHECKS = 1;
