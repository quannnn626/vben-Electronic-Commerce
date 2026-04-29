/*
 Navicat Premium Data Transfer

 Source Server         : MySQL8.0
 Source Server Type    : MySQL
 Source Server Version : 80019
 Source Host           : localhost:3307
 Source Schema         : vben_mall

 Target Server Type    : MySQL
 Target Server Version : 80019
 File Encoding         : 65001

 Date: 29/04/2026 16:35:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mall_file
-- ----------------------------
DROP TABLE IF EXISTS `mall_file`;
CREATE TABLE `mall_file`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '附件ID（主键）',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '附件名称',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '附件访问路径',
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '附件类型',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` int NULL DEFAULT 0 COMMENT '状态：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '附件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mall_file
-- ----------------------------
INSERT INTO `mall_file` VALUES (11, '10月31日 (1).mp4', '/upload/20260424172524_4615a82c2a73433a9e0246cfb16ce1f0.mp4', 'video/mp4', '2026-04-24 17:25:25', '2026-04-24 17:25:25', 0);
INSERT INTO `mall_file` VALUES (12, 'QQ图片20230426170644.jpg', '/upload/20260424172600_e89eae1061dc4293bb5562e06a63114a.jpg', 'image/jpeg', '2026-04-24 17:26:01', '2026-04-24 17:26:01', 0);
INSERT INTO `mall_file` VALUES (13, 'QQ图片20230426170644.jpg', '/upload/20260424173833_6197e5e1ba8845e5a324f81a0166fe22.jpg', 'image/jpeg', '2026-04-24 17:38:34', '2026-04-24 17:38:34', 0);
INSERT INTO `mall_file` VALUES (14, '微信图片_20250413113719.png', '/upload/20260424173838_a2b3394ce25447eb8a855795d41de623.png', 'image/png', '2026-04-24 17:38:38', '2026-04-24 17:38:38', 0);
INSERT INTO `mall_file` VALUES (15, 'QQ图片20230426170644.jpg', '/upload/20260424174354_f6fc18b1319f4b588f50675af2cd9fe2.jpg', 'image/jpeg', '2026-04-24 17:43:55', '2026-04-24 17:43:55', 0);
INSERT INTO `mall_file` VALUES (16, '微信图片_20250413113719.png', '/upload/20260424174356_2adbccbbee054fe1bd7394dd8b7a9051.png', 'image/png', '2026-04-24 17:43:57', '2026-04-24 17:43:57', 0);
INSERT INTO `mall_file` VALUES (17, 'Zephyrus Duo 15 x ZЯØFØRM_3840x2160.jpg', '/upload/20260424174358_caa72f6e0e2848a1a0dafdea48044a42.jpg', 'image/jpeg', '2026-04-24 17:43:59', '2026-04-24 17:43:59', 0);
INSERT INTO `mall_file` VALUES (18, '10月31日 (1).mp4', '/upload/20260424174405_03613bac947f4ff08ffcc977825a03c3.mp4', 'video/mp4', '2026-04-24 17:44:06', '2026-04-24 17:44:06', 0);

-- ----------------------------
-- Table structure for mall_order
-- ----------------------------
DROP TABLE IF EXISTS `mall_order`;
CREATE TABLE `mall_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '实付金额',
  `status` tinyint NOT NULL COMMENT '0待支付 1已支付 2已发货 3已完成 4已取消',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人（快照）',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号（快照）',
  `receiver_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '地址（快照）',
  `address_id` bigint NULL DEFAULT NULL COMMENT '地址ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `delivery_time` datetime NULL DEFAULT NULL COMMENT '发货时间',
  `finish_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付方式',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mall_order
-- ----------------------------
INSERT INTO `mall_order` VALUES (3, '0C266DFCB4194590', 2046463574828482561, 22564.00, 22564.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 15:35:56', '2026-04-27 15:35:56', NULL, NULL, NULL, '2026-04-27 15:35:56', 0, NULL, NULL);
INSERT INTO `mall_order` VALUES (4, 'F7426ED929024B8C', 2046463574828482561, 22564.00, 22564.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 15:39:28', '2026-04-27 15:39:28', NULL, NULL, NULL, '2026-04-27 15:39:28', 0, NULL, NULL);
INSERT INTO `mall_order` VALUES (5, '073EFF83693A4386', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 16:57:34', '2026-04-27 16:57:34', NULL, NULL, NULL, '2026-04-27 16:57:34', 0, NULL, NULL);
INSERT INTO `mall_order` VALUES (6, '6372628D67294427', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 16:58:03', '2026-04-27 16:58:03', NULL, NULL, NULL, '2026-04-27 16:58:03', 0, NULL, NULL);
INSERT INTO `mall_order` VALUES (7, '3D727F67313F4E0F', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 17:05:37', '2026-04-27 17:05:37', NULL, NULL, NULL, '2026-04-27 17:05:37', 0, NULL, NULL);
INSERT INTO `mall_order` VALUES (8, '7FA18A42A91E41AE', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 17:06:21', '2026-04-27 17:06:21', NULL, NULL, NULL, '2026-04-27 17:06:21', 0, NULL, NULL);
INSERT INTO `mall_order` VALUES (9, 'C9674A51F88348B0', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 17:20:19', '2026-04-27 17:20:19', NULL, NULL, NULL, '2026-04-27 17:20:19', 0, 'alipay', '');
INSERT INTO `mall_order` VALUES (10, '92FE686C19D14FF3', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 17:21:33', '2026-04-27 17:21:33', NULL, NULL, NULL, '2026-04-27 17:21:33', 0, 'alipay', '');
INSERT INTO `mall_order` VALUES (11, '8601157244FF4399', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 17:31:07', '2026-04-27 17:31:07', NULL, NULL, NULL, '2026-04-27 17:31:07', 0, 'alipay', '');
INSERT INTO `mall_order` VALUES (12, '563072E9F1AD45C8', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 17:34:57', '2026-04-27 17:34:57', NULL, NULL, NULL, '2026-04-27 17:34:57', 0, 'alipay', '');
INSERT INTO `mall_order` VALUES (13, 'A2F7EAD14EC947FB', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 17:38:25', '2026-04-27 17:38:25', NULL, NULL, NULL, '2026-04-27 17:38:25', 0, 'alipay', '');
INSERT INTO `mall_order` VALUES (14, '006021A8088D4A11', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 17:39:01', '2026-04-27 17:39:01', NULL, NULL, NULL, '2026-04-27 17:39:01', 0, 'alipay', '');
INSERT INTO `mall_order` VALUES (15, '610937D24F514D48', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 17:45:56', '2026-04-27 17:45:56', NULL, NULL, NULL, '2026-04-27 17:45:56', 0, 'alipay', '');
INSERT INTO `mall_order` VALUES (16, 'B27D167DD3414DDE', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 17:57:07', '2026-04-27 17:57:07', NULL, NULL, NULL, '2026-04-27 17:57:07', 0, 'alipay', '');
INSERT INTO `mall_order` VALUES (17, '77CCA98419F44C06', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 17:59:27', '2026-04-27 17:59:27', NULL, NULL, NULL, '2026-04-27 17:59:27', 0, 'alipay', '');
INSERT INTO `mall_order` VALUES (18, 'C08188DECE3B4B92', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 18:00:32', '2026-04-27 18:00:32', NULL, NULL, NULL, '2026-04-27 18:00:32', 0, 'alipay', '');
INSERT INTO `mall_order` VALUES (19, 'C3AD3AA323DF4EBE', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 20:14:10', '2026-04-27 20:14:10', NULL, NULL, NULL, '2026-04-27 20:14:10', 0, 'alipay', '');
INSERT INTO `mall_order` VALUES (20, 'D07DBE36613C4E68', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 20:14:19', '2026-04-27 20:14:19', NULL, NULL, NULL, '2026-04-27 20:14:19', 0, 'alipay', '');
INSERT INTO `mall_order` VALUES (21, 'B66C6386D38648B7', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 20:22:49', '2026-04-27 20:22:49', NULL, NULL, NULL, '2026-04-27 20:22:49', 0, 'wechat', '');
INSERT INTO `mall_order` VALUES (22, '77200899D0764AB0', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 20:23:53', '2026-04-27 20:23:53', NULL, NULL, NULL, '2026-04-27 20:23:53', 0, 'alipay', '');
INSERT INTO `mall_order` VALUES (23, '74155915379F468D', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 20:30:58', '2026-04-27 20:30:58', NULL, NULL, NULL, '2026-04-27 20:30:58', 0, 'alipay', '');
INSERT INTO `mall_order` VALUES (24, '4BF36227E8F741E2', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 21:05:30', '2026-04-27 21:05:30', NULL, NULL, NULL, '2026-04-27 21:05:30', 0, 'alipay', '');
INSERT INTO `mall_order` VALUES (25, 'B0E25D08A1BD4844', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 21:06:21', '2026-04-27 21:06:21', NULL, NULL, NULL, '2026-04-27 21:06:21', 0, 'alipay', '');
INSERT INTO `mall_order` VALUES (26, '37278C5712FE44CE', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 21:07:03', '2026-04-27 21:07:03', NULL, NULL, NULL, '2026-04-27 21:07:03', 0, 'alipay', '');
INSERT INTO `mall_order` VALUES (27, '5B07AB86D6394C5A', 2046463574828482561, 11282.00, 11282.00, 1, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-04-27 21:07:33', '2026-04-27 21:07:33', NULL, NULL, NULL, '2026-04-27 21:07:33', 0, 'alipay', '');

-- ----------------------------
-- Table structure for mall_order_item
-- ----------------------------
DROP TABLE IF EXISTS `mall_order_item`;
CREATE TABLE `mall_order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称快照',
  `product_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片快照',
  `sku_spec_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'SKU规格名称快照',
  `price` decimal(10, 2) NOT NULL COMMENT '单价',
  `quantity` int NOT NULL COMMENT '数量',
  `total_price` decimal(10, 2) NOT NULL COMMENT '小计',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mall_order_item
-- ----------------------------
INSERT INTO `mall_order_item` VALUES (1, 3, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 2, 22564.00, '2026-04-27 15:35:56', '2026-04-27 15:35:56', 0);
INSERT INTO `mall_order_item` VALUES (2, 4, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 2, 22564.00, '2026-04-27 15:39:28', '2026-04-27 15:39:28', 0);
INSERT INTO `mall_order_item` VALUES (3, 5, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 16:57:34', '2026-04-27 16:57:34', 0);
INSERT INTO `mall_order_item` VALUES (4, 6, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 16:58:03', '2026-04-27 16:58:03', 0);
INSERT INTO `mall_order_item` VALUES (5, 7, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 17:05:37', '2026-04-27 17:05:37', 0);
INSERT INTO `mall_order_item` VALUES (6, 8, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 17:06:21', '2026-04-27 17:06:21', 0);
INSERT INTO `mall_order_item` VALUES (7, 9, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 17:20:19', '2026-04-27 17:20:19', 0);
INSERT INTO `mall_order_item` VALUES (8, 10, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 17:21:33', '2026-04-27 17:21:33', 0);
INSERT INTO `mall_order_item` VALUES (9, 11, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 17:31:07', '2026-04-27 17:31:07', 0);
INSERT INTO `mall_order_item` VALUES (10, 12, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 17:34:57', '2026-04-27 17:34:57', 0);
INSERT INTO `mall_order_item` VALUES (11, 13, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 17:38:25', '2026-04-27 17:38:25', 0);
INSERT INTO `mall_order_item` VALUES (12, 14, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 17:39:01', '2026-04-27 17:39:01', 0);
INSERT INTO `mall_order_item` VALUES (13, 15, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 17:45:56', '2026-04-27 17:45:56', 0);
INSERT INTO `mall_order_item` VALUES (14, 16, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 17:57:07', '2026-04-27 17:57:07', 0);
INSERT INTO `mall_order_item` VALUES (15, 17, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 17:59:27', '2026-04-27 17:59:27', 0);
INSERT INTO `mall_order_item` VALUES (16, 18, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 18:00:32', '2026-04-27 18:00:32', 0);
INSERT INTO `mall_order_item` VALUES (17, 19, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 20:14:10', '2026-04-27 20:14:10', 0);
INSERT INTO `mall_order_item` VALUES (18, 20, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 20:14:19', '2026-04-27 20:14:19', 0);
INSERT INTO `mall_order_item` VALUES (19, 21, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 20:22:49', '2026-04-27 20:22:49', 0);
INSERT INTO `mall_order_item` VALUES (20, 22, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 20:23:53', '2026-04-27 20:23:53', 0);
INSERT INTO `mall_order_item` VALUES (21, 23, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 20:30:58', '2026-04-27 20:30:58', 0);
INSERT INTO `mall_order_item` VALUES (22, 24, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 21:05:30', '2026-04-27 21:05:30', 0);
INSERT INTO `mall_order_item` VALUES (23, 25, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 21:06:21', '2026-04-27 21:06:21', 0);
INSERT INTO `mall_order_item` VALUES (24, 26, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 21:07:03', '2026-04-27 21:07:03', 0);
INSERT INTO `mall_order_item` VALUES (25, 27, 7, 'ROG', NULL, 'U7 255HX/3060', 11282.00, 1, 11282.00, '2026-04-27 21:07:33', '2026-04-27 21:07:33', 0);

-- ----------------------------
-- Table structure for mall_product
-- ----------------------------
DROP TABLE IF EXISTS `mall_product`;
CREATE TABLE `mall_product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '商品描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1上架 0下架',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mall_product
-- ----------------------------
INSERT INTO `mall_product` VALUES (7, 'ROG', 'ROG魔霸系列', 1, '2026-04-24 17:45:22', '2026-04-24 17:45:22', 0);

-- ----------------------------
-- Table structure for mall_product_category
-- ----------------------------
DROP TABLE IF EXISTS `mall_product_category`;
CREATE TABLE `mall_product_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '类目ID（主键）',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类目名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父级类目ID（0表示顶级类目）',
  `level` int NULL DEFAULT 1 COMMENT '类目层级',
  `sort` int NULL DEFAULT 0 COMMENT '排序值',
  `status` int NULL DEFAULT 1 COMMENT '状态：1-正常 0-禁用',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类目图标URL',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 101318 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品类目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mall_product_category
-- ----------------------------
INSERT INTO `mall_product_category` VALUES (1, '艺术品', 0, 1, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (2, '珠宝首饰', 0, 1, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (3, '古玩杂项', 0, 1, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (4, '名表名包', 0, 1, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (5, '房产汽车', 0, 1, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (6, '酒类收藏', 0, 1, 6, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (7, '邮品钱币', 0, 1, 7, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (8, '古籍善本', 0, 1, 8, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (9, '现当代艺术', 0, 1, 9, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (10, '奢侈品', 0, 1, 10, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (11, '书画', 1, 2, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (12, '陶瓷', 1, 2, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (13, '玉器', 1, 2, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (14, '雕塑', 1, 2, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (15, '当代艺术', 1, 2, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (21, '翡翠玉石', 2, 2, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (22, '钻石', 2, 2, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (23, '黄金', 2, 2, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (24, '彩色宝石', 2, 2, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (25, '珍珠', 2, 2, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (31, '青铜器', 3, 2, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (32, '钱币', 3, 2, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (33, '文房四宝', 3, 2, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (34, '紫砂壶', 3, 2, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (35, '竹木牙角', 3, 2, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (41, '瑞士名表', 4, 2, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (42, '古董表', 4, 2, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (43, '奢侈包袋', 4, 2, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (44, '配饰', 4, 2, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (51, '房产', 5, 2, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (52, '汽车', 5, 2, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (53, '摩托车', 5, 2, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (61, '白酒', 6, 2, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (62, '红酒', 6, 2, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (63, '威士忌', 6, 2, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (71, '邮票', 7, 2, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (72, '邮封', 7, 2, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (73, '邮戳', 7, 2, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (74, '钱币', 7, 2, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (75, '纸币', 7, 2, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (81, '古籍', 8, 2, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (82, '善本', 8, 2, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (83, '碑帖', 8, 2, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (84, '信札', 8, 2, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (85, '手稿', 8, 2, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (91, '当代绘画', 9, 2, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (92, '当代雕塑', 9, 2, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (93, '装置艺术', 9, 2, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (94, '影像艺术', 9, 2, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (95, '行为艺术', 9, 2, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (101, '高级定制', 10, 2, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (102, '珠宝', 10, 2, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (103, '腕表', 10, 2, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (104, '皮具', 10, 2, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (105, '配饰', 10, 2, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (111, '国画', 11, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (112, '油画', 11, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (113, '书法', 11, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (114, '版画', 11, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (115, '水彩画', 11, 3, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (121, '明清瓷器', 12, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (122, '现代陶瓷', 12, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (123, '官窑瓷器', 12, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (124, '民窑瓷器', 12, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (131, '和田玉', 13, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (132, '翡翠', 13, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (133, '岫玉', 13, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (134, '古玉', 13, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (141, '石雕', 14, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (142, '木雕', 14, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (143, '铜雕', 14, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (144, '玉雕', 14, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (145, '现代雕塑', 14, 3, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (151, '抽象艺术', 15, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (152, '观念艺术', 15, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (153, '新媒体艺术', 15, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (200, '钟表收藏', 0, 1, 11, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (201, '茶具收藏', 0, 1, 12, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (202, '文玩收藏', 0, 1, 13, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (203, '红色收藏', 0, 1, 14, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (204, '当代工艺品', 0, 1, 15, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (211, '翡翠手镯', 21, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (212, '挂件吊坠', 21, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (213, '翡翠戒指', 21, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (214, '翡翠项链', 21, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (215, '翡翠耳环', 21, 3, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (221, '钻戒', 22, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (222, '钻石项链', 22, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (223, '钻石耳环', 22, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (224, '钻石手链', 22, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (231, '黄金饰品', 23, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (232, '金条', 23, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (233, '金币', 23, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (241, '红宝石', 24, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (242, '蓝宝石', 24, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (243, '祖母绿', 24, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (244, '碧玺', 24, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (245, '坦桑石', 24, 3, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (246, '其他彩色宝石', 24, 3, 6, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (251, '海水珍珠', 25, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (252, '淡水珍珠', 25, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (253, '南洋珍珠', 25, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (254, '大溪地黑珍珠', 25, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (255, '古董珍珠', 25, 3, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (311, '青铜礼器', 31, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (312, '青铜兵器', 31, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (313, '青铜杂器', 31, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (321, '古钱币', 32, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (322, '机制币', 32, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (323, '纸币', 32, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (324, '纪念币', 32, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (331, '毛笔', 33, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (332, '墨', 33, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (333, '纸', 33, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (334, '砚台', 33, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (341, '明清紫砂', 34, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (342, '现代紫砂', 34, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (343, '名家紫砂', 34, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (344, '紫砂摆件', 34, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (351, '竹雕', 35, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (352, '木雕', 35, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (353, '象牙', 35, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (354, '角雕', 35, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (355, '核雕', 35, 3, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (411, '百达翡丽', 41, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (412, '劳力士', 41, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (413, '江诗丹顿', 41, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (414, '欧米茄', 41, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (421, '怀表', 42, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (422, '座钟', 42, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (423, '古董腕表', 42, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (424, '限量版', 42, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (431, '爱马仕', 43, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (432, '香奈儿', 43, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (433, '路易威登', 43, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (434, '古驰', 43, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (441, '太阳镜', 44, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (442, '丝巾', 44, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (443, '腰带', 44, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (444, '钱包', 44, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (445, '卡包', 44, 3, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (511, '住宅', 51, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (512, '商业地产', 51, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (513, '土地', 51, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (514, '别墅', 51, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (515, '商铺', 51, 3, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (516, '写字楼', 51, 3, 6, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (521, '轿车', 52, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (522, 'SUV', 52, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (523, '跑车', 52, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (524, '古董车', 52, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (525, '豪华车', 52, 3, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (526, '新能源车', 52, 3, 6, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (531, '哈雷', 53, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (532, '杜卡迪', 53, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (533, '宝马摩托', 53, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (534, '其他品牌', 53, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (611, '茅台', 61, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (612, '五粮液', 61, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (613, '剑南春', 61, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (614, '泸州老窖', 61, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (615, '洋河', 61, 3, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (616, '郎酒', 61, 3, 6, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (617, '汾酒', 61, 3, 7, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (618, '古井贡酒', 61, 3, 8, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (619, '其他名酒', 61, 3, 9, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (621, '拉菲', 62, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (622, '拉图', 62, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (623, '玛歌', 62, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (624, '木桐', 62, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (625, '奥比昂', 62, 3, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (626, '勃艮第', 62, 3, 6, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (627, '其他红酒', 62, 3, 7, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (631, '苏格兰威士忌', 63, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (632, '日本威士忌', 63, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (633, '美国威士忌', 63, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (634, '其他威士忌', 63, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (711, '清代邮票', 71, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (712, '民国邮票', 71, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (713, '新中国邮票', 71, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (714, '外国邮票', 71, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (715, '特种邮票', 71, 3, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (716, '纪念邮票', 71, 3, 6, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (721, '实寄封', 72, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (722, '首日封', 72, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (723, '纪念封', 72, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (724, '其他邮封', 72, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (731, '日戳', 73, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (732, '纪念戳', 73, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (733, '风景戳', 73, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (734, '其他邮戳', 73, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (741, '古钱币', 74, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (742, '机制币', 74, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (743, '银元', 74, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (744, '铜币', 74, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (745, '纪念币', 74, 3, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (751, '第一套人民币', 75, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (752, '第二套人民币', 75, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (753, '第三套人民币', 75, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (754, '第四套人民币', 75, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (755, '第五套人民币', 75, 3, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (756, '外汇券', 75, 3, 6, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (757, '外国纸币', 75, 3, 7, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (811, '宋刻本', 81, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (812, '元刻本', 81, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (813, '明刻本', 81, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (814, '清刻本', 81, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (815, '民国刻本', 81, 3, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (816, '抄本', 81, 3, 6, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (821, '经部', 82, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (822, '史部', 82, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (823, '子部', 82, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (824, '集部', 82, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (831, '碑拓', 83, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (832, '帖拓', 83, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (833, '其他碑帖', 83, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (841, '名人信札', 84, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (842, '历史信札', 84, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (843, '其他信札', 84, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (851, '名人手稿', 85, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (852, '历史手稿', 85, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (853, '其他手稿', 85, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (911, '抽象绘画', 91, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (912, '写实绘画', 91, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (913, '表现主义', 91, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (914, '超现实主义', 91, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (915, '波普艺术', 91, 3, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (921, '金属雕塑', 92, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (922, '石雕', 92, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (923, '木雕', 92, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (924, '其他雕塑', 92, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (931, '装置艺术', 93, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (932, '其他装置', 93, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (941, '摄影作品', 94, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (942, '视频艺术', 94, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (943, '其他影像', 94, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (951, '行为艺术', 95, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (952, '其他行为', 95, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (1011, '高级时装', 101, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (1012, '定制礼服', 101, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (1013, '定制西装', 101, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (1021, '高级珠宝', 102, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (1022, '其他珠宝', 102, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (1031, '高级腕表', 103, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (1032, '其他腕表', 103, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (1041, '高级皮具', 104, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (1042, '其他皮具', 104, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (1051, '高级配饰', 105, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (1052, '其他配饰', 105, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (2001, '怀表', 200, 2, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (2002, '座钟', 200, 2, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (2003, '挂钟', 200, 2, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (2011, '紫砂茶具', 201, 2, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (2012, '瓷器茶具', 201, 2, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (2013, '银器茶具', 201, 2, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (2014, '其他茶具', 201, 2, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (2021, '手串', 202, 2, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (2022, '把件', 202, 2, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (2023, '摆件', 202, 2, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (2024, '其他文玩', 202, 2, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (2031, '红色文献', 203, 2, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (2032, '红色徽章', 203, 2, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (2033, '红色纪念品', 203, 2, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (2041, '当代陶瓷', 204, 2, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (2042, '当代漆器', 204, 2, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (2043, '当代金属工艺', 204, 2, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (2044, '其他工艺品', 204, 2, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (4124, '真力时', 41, 3, 13, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (4125, '百年灵', 41, 3, 14, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (4126, '宇舶', 41, 3, 15, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (4127, '萧邦', 41, 3, 16, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (4128, '伯爵', 41, 3, 17, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (4129, '其他品牌', 41, 3, 18, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (20111, '紫砂壶', 2011, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (20112, '紫砂杯', 2011, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (20113, '紫砂茶盘', 2011, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (20114, '其他紫砂', 2011, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (20121, '青花茶具', 2012, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (20122, '粉彩茶具', 2012, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (20123, '单色釉茶具', 2012, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (20124, '其他瓷器茶具', 2012, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (20211, '小叶紫檀', 2021, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (20212, '海南黄花梨', 2021, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (20213, '金丝楠', 2021, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (20214, '沉香', 2021, 3, 4, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (20215, '崖柏', 2021, 3, 5, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (20216, '菩提', 2021, 3, 6, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (20217, '其他手串', 2021, 3, 7, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (20221, '和田玉把件', 2022, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (20222, '翡翠把件', 2022, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (20223, '其他把件', 2022, 3, 3, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (41111, '百达翡丽', 4111, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (41112, '其他百达翡丽', 4111, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (41121, '劳力士', 4112, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (41122, '其他劳力士', 4112, 3, 2, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (101313, '义乌出品', 2041, 3, 1, 1, NULL, '2026-04-21 21:31:19', '2026-04-21 21:31:19', 0);
INSERT INTO `mall_product_category` VALUES (101314, '义务', 0, 1, 16, 1, '义乌出品', '2026-04-22 10:57:51', '2026-04-22 10:57:51', 0);
INSERT INTO `mall_product_category` VALUES (101315, '日用', 101314, 2, 1, 1, '日用品', '2026-04-22 10:58:53', '2026-04-22 10:58:53', 0);
INSERT INTO `mall_product_category` VALUES (101316, '雨伞', 101315, 3, 1, 1, '雨伞', '2026-04-22 11:03:51', '2026-04-22 11:03:51', 0);
INSERT INTO `mall_product_category` VALUES (101317, '纸巾', 101315, 3, 2, 1, '餐巾纸', '2026-04-22 11:06:26', '2026-04-22 11:06:26', 0);

-- ----------------------------
-- Table structure for mall_product_category_rel
-- ----------------------------
DROP TABLE IF EXISTS `mall_product_category_rel`;
CREATE TABLE `mall_product_category_rel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `category_id` bigint NOT NULL COMMENT '类目ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2047612856616968208 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品-类目关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mall_product_category_rel
-- ----------------------------
INSERT INTO `mall_product_category_rel` VALUES (2047612856549859329, 7, 111);
INSERT INTO `mall_product_category_rel` VALUES (2047612856549859330, 7, 112);
INSERT INTO `mall_product_category_rel` VALUES (2047612856549859331, 7, 113);
INSERT INTO `mall_product_category_rel` VALUES (2047612856549859332, 7, 114);
INSERT INTO `mall_product_category_rel` VALUES (2047612856549859333, 7, 115);
INSERT INTO `mall_product_category_rel` VALUES (2047612856549859334, 7, 121);
INSERT INTO `mall_product_category_rel` VALUES (2047612856616968193, 7, 122);
INSERT INTO `mall_product_category_rel` VALUES (2047612856616968194, 7, 123);
INSERT INTO `mall_product_category_rel` VALUES (2047612856616968195, 7, 124);
INSERT INTO `mall_product_category_rel` VALUES (2047612856616968196, 7, 131);
INSERT INTO `mall_product_category_rel` VALUES (2047612856616968197, 7, 132);
INSERT INTO `mall_product_category_rel` VALUES (2047612856616968198, 7, 133);
INSERT INTO `mall_product_category_rel` VALUES (2047612856616968199, 7, 134);
INSERT INTO `mall_product_category_rel` VALUES (2047612856616968200, 7, 141);
INSERT INTO `mall_product_category_rel` VALUES (2047612856616968201, 7, 142);
INSERT INTO `mall_product_category_rel` VALUES (2047612856616968202, 7, 143);
INSERT INTO `mall_product_category_rel` VALUES (2047612856616968203, 7, 144);
INSERT INTO `mall_product_category_rel` VALUES (2047612856616968204, 7, 145);
INSERT INTO `mall_product_category_rel` VALUES (2047612856616968205, 7, 151);
INSERT INTO `mall_product_category_rel` VALUES (2047612856616968206, 7, 152);
INSERT INTO `mall_product_category_rel` VALUES (2047612856616968207, 7, 153);

-- ----------------------------
-- Table structure for mall_sku
-- ----------------------------
DROP TABLE IF EXISTS `mall_sku`;
CREATE TABLE `mall_sku`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
  `product_id` bigint NOT NULL COMMENT '所属商品ID',
  `price` decimal(10, 2) NOT NULL COMMENT 'SKU价格',
  `stock` int NOT NULL DEFAULT 0 COMMENT '可用库存',
  `locked_stock` int NOT NULL DEFAULT 0 COMMENT '锁定库存',
  `spec_data` json NOT NULL COMMENT '规格数据(JSON)',
  `sku_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'SKU编码',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品SKU表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mall_sku
-- ----------------------------
INSERT INTO `mall_sku` VALUES (6, 7, 11282.00, 100, 0, '{\"name\": \"U7 255HX/3060\", \"fileId\": 18}', '', 1, '2026-04-24 17:45:22', '2026-04-24 17:45:22');
INSERT INTO `mall_sku` VALUES (7, 7, 9998.00, 100, 0, '{\"name\": \"R9 8949HX/3060\", \"fileId\": 16}', '', 1, '2026-04-24 17:45:22', '2026-04-24 17:45:22');
INSERT INTO `mall_sku` VALUES (8, 7, 10122.00, 94, 0, '{\"name\": \"R9 8940HX/3070Ti\", \"fileId\": 17}', '', 1, '2026-04-24 17:45:22', '2026-04-25 21:38:42');

-- ----------------------------
-- Table structure for mall_user_address
-- ----------------------------
DROP TABLE IF EXISTS `mall_user_address`;
CREATE TABLE `mall_user_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '省',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '区',
  `detail_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `is_default` tinyint NULL DEFAULT 0 COMMENT '是否默认地址 1是 0否',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户地址表（版本化）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mall_user_address
-- ----------------------------
INSERT INTO `mall_user_address` VALUES (1, 2046463574828482561, '张三', '13800000001', '广东省', '深圳市', '南山区', '科技园科苑路15号', 0, '2026-04-26 22:00:00', '2026-04-27 14:18:31', 0);
INSERT INTO `mall_user_address` VALUES (2, 2046463574828482561, '李四', '13900000002', '广东省', '广州市', '天河区', '珠江新城花城大道88号', 0, '2026-04-26 22:05:00', '2026-04-27 22:26:07', 0);
INSERT INTO `mall_user_address` VALUES (3, 2046463574828482561, 'y', '19987665633', '浙江省', '嘉兴市', '嘉善县', '宇智波幼儿园', 1, '2026-04-27 14:27:16', '2026-04-27 22:26:08', 0);
INSERT INTO `mall_user_address` VALUES (4, 2046463574828482561, 'yyy', '16674532231', '浙江省', '嘉兴市', '嘉善县', '浙江省嘉兴市嘉善县', 0, '2026-04-29 14:37:41', '2026-04-29 14:37:41', 0);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID（主键）',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录密码（建议加密存储）',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像地址',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户角色：admin-管理员 user-普通用户',
  `home_path` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '/analytics' COMMENT '登录后默认首页路径',
  `user_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户简介',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2047131275460681730 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (2046463574828482561, 'vben', '$2a$10$vj1MurvaDi6sMAycJOA8geMyUVdH.smxVC43ZF9idADeVE//FiV3e', 'Super', '', 'super', '/analytics', '', 1, '2026-04-19 19:06:22', '2026-04-26 16:55:44', 0);
INSERT INTO `sys_user` VALUES (2046495738450235393, 'test', '$2a$10$81x9rMP5EKA0xP4zO2/jSuM9OyuUKQSXWHZ.bMdjTpy7Qnki0MlFe', 'User', NULL, 'user', '/analytics', NULL, 1, '2026-04-21 15:46:21', '2026-04-23 09:50:11', 0);
INSERT INTO `sys_user` VALUES (2047131275460681729, 'user', '$2a$10$PKAE5Cd4tkv.hAEHDLlcd.oW5S4B8fYHkj/brPisHKWaF6WaVQgFq', '用户', NULL, 'user', '/analytics', NULL, 1, '2026-04-23 09:51:45', '2026-04-23 09:51:45', 0);

SET FOREIGN_KEY_CHECKS = 1;
