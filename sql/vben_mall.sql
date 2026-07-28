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

 Date: 28/07/2026 15:59:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mall_after_sale
-- ----------------------------
DROP TABLE IF EXISTS `mall_after_sale`;
CREATE TABLE `mall_after_sale`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `after_sale_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '售后单号',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `order_item_id` bigint NOT NULL COMMENT '订单商品ID',
  `user_id` bigint NOT NULL COMMENT '申请用户ID',
  `type` tinyint NOT NULL COMMENT '售后类型',
  `status` tinyint NOT NULL COMMENT '售后状态',
  `reason` tinyint NOT NULL COMMENT '售后原因',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '问题描述',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '售后数量',
  `refund_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '退款金额',
  `audit_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审核备注',
  `audit_user` bigint NULL DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单售后表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_after_sale
-- ----------------------------

-- ----------------------------
-- Table structure for mall_cart
-- ----------------------------
DROP TABLE IF EXISTS `mall_cart`;
CREATE TABLE `mall_cart`  (
  `id` bigint NOT NULL COMMENT '购物车ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '购买数量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_sku_id`(`sku_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '购物车表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_cart
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '附件表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_file
-- ----------------------------
INSERT INTO `mall_file` VALUES (2, 'QQ图片20230426170644.jpg', '/upload/20260727114025_6858215e4f324a87aa24ce66bae74621.jpg', 'image/jpeg', '2026-07-27 11:40:26', '2026-07-27 11:41:03', 0);
INSERT INTO `mall_file` VALUES (3, 'u=1755718785,2557578010&fm=253&app=138&f=JPEG.png', '/upload/20260727114025_2019d45fa46e460c8251ef31e2a16042.png', 'image/png', '2026-07-27 11:40:26', '2026-07-27 11:41:03', 0);
INSERT INTO `mall_file` VALUES (4, 'Zephyrus Duo 15 x ZЯØFØRM_3840x2160.jpg', '/upload/20260727114025_7fca222c69b748a0a71e6a1cf00c17d5.jpg', 'image/jpeg', '2026-07-27 11:40:26', '2026-07-27 11:41:03', 0);
INSERT INTO `mall_file` VALUES (5, 'Zephyrus Duo 15 x ZЯØFØRM_3840x2160.jpg', '/upload/20260728142853_616646a7d10a4e2bb4c8b7b96a9694cb.jpg', 'image/jpeg', '2026-07-28 14:28:54', '2026-07-28 14:29:15', 0);
INSERT INTO `mall_file` VALUES (6, '99195447cc7a803b26497db4fd5749233546867556157528.jpg', '/upload/20260728142853_dd1d29b614604d258b96f7cdfc85d097.jpg', 'image/jpeg', '2026-07-28 14:28:54', '2026-07-28 14:29:15', 0);
INSERT INTO `mall_file` VALUES (7, '微信图片_20250413113719.png', '/upload/20260728142853_1ed65863fbd74ff4ab833b17dd140749.png', 'image/png', '2026-07-28 14:28:54', '2026-07-28 14:29:15', 0);
INSERT INTO `mall_file` VALUES (8, '屏幕截图(472).png', '/upload/20260728143057_1fce8e7cd2fa4789ab886189333a66bc.png', 'image/png', '2026-07-28 14:30:57', '2026-07-28 14:31:20', 0);
INSERT INTO `mall_file` VALUES (9, '屏幕截图(478).png', '/upload/20260728143057_80851c37e56f4addb147b6de2d6ed3bb.png', 'image/png', '2026-07-28 14:30:57', '2026-07-28 14:31:20', 0);
INSERT INTO `mall_file` VALUES (10, '屏幕截图(479).png', '/upload/20260728143057_17a6ec364ec14230af22e024d508a83b.png', 'image/png', '2026-07-28 14:30:57', '2026-07-28 14:31:20', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_order
-- ----------------------------
INSERT INTO `mall_order` VALUES (1, '20260728143250716526', 2046463574828482561, 36330.00, 36330.00, 5, 'y', '19987665633', '浙江省嘉兴市嘉善县宇智波幼儿园', 3, '2026-07-28 14:32:50', '2026-07-28 14:32:57', '2026-07-28 14:38:12', NULL, NULL, '2026-07-28 14:38:12', 0, NULL, NULL);

-- ----------------------------
-- Table structure for mall_order_delivery
-- ----------------------------
DROP TABLE IF EXISTS `mall_order_delivery`;
CREATE TABLE `mall_order_delivery`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `logistics_company` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '物流公司',
  `tracking_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '物流单号',
  `delivery_status` tinyint NULL DEFAULT 0 COMMENT '物流状态',
  `delivery_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发货备注',
  `delivery_user` bigint NULL DEFAULT NULL COMMENT '发货人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `order_item_id` bigint NULL DEFAULT NULL COMMENT '订单商品ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_order_delivery
-- ----------------------------
INSERT INTO `mall_order_delivery` VALUES (2081991918260920321, 1, '3', '202607281432507165263', 0, NULL, 2046463574828482561, '2026-07-28 14:35:30', '2026-07-28 14:35:30', 0, 3);
INSERT INTO `mall_order_delivery` VALUES (2081992597922717697, 1, '2', '202607281432507165262', 0, NULL, 2046463574828482561, '2026-07-28 14:38:12', '2026-07-28 14:38:12', 0, 2);
INSERT INTO `mall_order_delivery` VALUES (2081992601622093826, 1, '1', '202607281432507165261', 0, NULL, 2046463574828482561, '2026-07-28 14:38:12', '2026-07-28 14:38:12', 0, 1);

-- ----------------------------
-- Table structure for mall_order_delivery_item
-- ----------------------------
DROP TABLE IF EXISTS `mall_order_delivery_item`;
CREATE TABLE `mall_order_delivery_item`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `delivery_id` bigint NOT NULL COMMENT '物流单ID',
  `order_item_id` bigint NOT NULL COMMENT '订单商品ID',
  `quantity` int NULL DEFAULT 1 COMMENT '发货数量',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_delivery`(`delivery_id` ASC) USING BTREE,
  INDEX `idx_order_item`(`order_item_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '物流商品关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mall_order_delivery_item
-- ----------------------------

-- ----------------------------
-- Table structure for mall_order_item
-- ----------------------------
DROP TABLE IF EXISTS `mall_order_item`;
CREATE TABLE `mall_order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称快照',
  `product_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片快照',
  `sku_spec_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'SKU规格名称快照',
  `price` decimal(10, 2) NOT NULL COMMENT '单价',
  `quantity` int NOT NULL COMMENT '数量',
  `total_price` decimal(10, 2) NOT NULL COMMENT '小计',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  `item_status` tinyint NULL DEFAULT 0 COMMENT '商品履约状态：0待发货 1已发货 2运输中 3已收货 4已完成 5售后中',
  `refund_quantity` int NULL DEFAULT 0 COMMENT '已退款数量',
  `product_id` bigint NULL DEFAULT NULL COMMENT '商品ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_order_item
-- ----------------------------
INSERT INTO `mall_order_item` VALUES (1, 1, 3, '管理员商品1', '/upload/20260727114025_7fca222c69b748a0a71e6a1cf00c17d5.jpg', '规格3', 7000.00, 2, 14000.00, '2026-07-28 14:32:50', '2026-07-28 14:32:50', 0, 0, 0, NULL);
INSERT INTO `mall_order_item` VALUES (2, 1, 2, '管理员商品1', '/upload/20260727114025_2019d45fa46e460c8251ef31e2a16042.png', '规格2', 6000.00, 2, 12000.00, '2026-07-28 14:32:50', '2026-07-28 14:32:50', 0, 0, 0, NULL);
INSERT INTO `mall_order_item` VALUES (3, 1, 1, '管理员商品1', '/upload/20260727114025_6858215e4f324a87aa24ce66bae74621.jpg', '规格1', 5000.00, 2, 10000.00, '2026-07-28 14:32:50', '2026-07-28 14:32:50', 0, 0, 0, NULL);
INSERT INTO `mall_order_item` VALUES (4, 1, 6, '用户商品1', '/upload/20260728142853_616646a7d10a4e2bb4c8b7b96a9694cb.jpg', '3', 44.00, 2, 88.00, '2026-07-28 14:32:50', '2026-07-28 14:32:50', 0, 0, 0, NULL);
INSERT INTO `mall_order_item` VALUES (5, 1, 5, '用户商品1', '/upload/20260728142853_dd1d29b614604d258b96f7cdfc85d097.jpg', '2', 33.00, 2, 66.00, '2026-07-28 14:32:50', '2026-07-28 14:32:50', 0, 0, 0, NULL);
INSERT INTO `mall_order_item` VALUES (6, 1, 4, '用户商品1', '/upload/20260728142853_dd1d29b614604d258b96f7cdfc85d097.jpg', '1', 22.00, 2, 44.00, '2026-07-28 14:32:50', '2026-07-28 14:32:50', 0, 0, 0, NULL);
INSERT INTO `mall_order_item` VALUES (7, 1, 9, '用户2商品', '/upload/20260728143057_17a6ec364ec14230af22e024d508a83b.png', '3', 33.00, 2, 66.00, '2026-07-28 14:32:50', '2026-07-28 14:32:50', 0, 0, 0, NULL);
INSERT INTO `mall_order_item` VALUES (8, 1, 8, '用户2商品', '/upload/20260728143057_80851c37e56f4addb147b6de2d6ed3bb.png', '2', 22.00, 2, 44.00, '2026-07-28 14:32:50', '2026-07-28 14:32:50', 0, 0, 0, NULL);
INSERT INTO `mall_order_item` VALUES (9, 1, 7, '用户2商品', '/upload/20260728143057_1fce8e7cd2fa4789ab886189333a66bc.png', '1', 11.00, 2, 22.00, '2026-07-28 14:32:50', '2026-07-28 14:32:50', 0, 0, 0, NULL);

-- ----------------------------
-- Table structure for mall_payment
-- ----------------------------
DROP TABLE IF EXISTS `mall_payment`;
CREATE TABLE `mall_payment`  (
  `id` bigint NOT NULL COMMENT '主键ID',
  `payment_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支付单号',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `pay_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支付方式',
  `amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '支付状态',
  `third_trade_no` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方交易号',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `callback_time` datetime NULL DEFAULT NULL COMMENT '回调时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_status`(`order_id` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_payment
-- ----------------------------
INSERT INTO `mall_payment` VALUES (2081991270672965635, 'PAY2081991270672965634', 1, '20260728143250716526', 2046463574828482561, 'alipay', 36330.00, 1, 'SIM1785220376694', '2026-07-28 14:32:57', '2026-07-28 14:32:57', NULL, '2026-07-28 14:32:55', '2026-07-28 14:32:55', 0);

-- ----------------------------
-- Table structure for mall_product
-- ----------------------------
DROP TABLE IF EXISTS `mall_product`;
CREATE TABLE `mall_product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '商品描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1上架 0下架',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_product
-- ----------------------------
INSERT INTO `mall_product` VALUES (1, '管理员商品1', '管理员添加商品', 1, 2046463574828482561, '2026-07-27 11:41:03', '2026-07-27 11:41:03', 0);
INSERT INTO `mall_product` VALUES (2, '用户商品1', '', 1, 2047131275460681729, '2026-07-28 14:29:15', '2026-07-28 14:29:15', 0);
INSERT INTO `mall_product` VALUES (3, '用户2商品', '', 1, 2046495738450235393, '2026-07-28 14:31:20', '2026-07-28 14:31:20', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 1026 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品类目表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_product_category
-- ----------------------------
INSERT INTO `mall_product_category` VALUES (1, '数码家电', 0, 1, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (2, '服饰鞋包', 0, 1, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (3, '美妆护肤', 0, 1, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (4, '食品生鲜', 0, 1, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (5, '家居百货', 0, 1, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (6, '母婴用品', 0, 1, 6, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (7, '运动户外', 0, 1, 7, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (8, '汽车用品', 0, 1, 8, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (9, '图书文娱', 0, 1, 9, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (10, '手机配件', 0, 1, 10, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (11, '手机整机', 1, 2, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (12, '电脑整机', 1, 2, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (13, '平板设备', 1, 2, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (14, '大家电', 1, 2, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (15, '小家电', 1, 2, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (21, '男装', 2, 2, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (22, '女装', 2, 2, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (23, '鞋靴', 2, 2, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (24, '箱包', 2, 2, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (25, '内衣配饰', 2, 2, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (31, '面部护肤', 3, 2, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (32, '彩妆美妆', 3, 2, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (33, '香水香氛', 3, 2, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (34, '身体护理', 3, 2, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (35, '个护工具', 3, 2, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (41, '新鲜果蔬', 4, 2, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (42, '肉蛋水产', 4, 2, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (43, '休闲零食', 4, 2, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (44, '酒水饮料', 4, 2, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (45, '粮油干货', 4, 2, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (51, '厨具餐具', 5, 2, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (52, '清洁用品', 5, 2, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (53, '家纺床品', 5, 2, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (54, '收纳置物', 5, 2, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (55, '日用杂货', 5, 2, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (61, '奶粉辅食', 6, 2, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (62, '尿裤湿巾', 6, 2, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (63, '婴童服饰', 6, 2, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (64, '母婴洗护', 6, 2, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (65, '玩具车床', 6, 2, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (71, '运动服饰', 7, 2, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (72, '健身器材', 7, 2, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (73, '球类运动', 7, 2, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (74, '户外装备', 7, 2, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (75, '骑行用品', 7, 2, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (81, '车内饰品', 8, 2, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (82, '车外养护', 8, 2, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (83, '车载电器', 8, 2, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (84, '维修工具', 8, 2, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (85, '安全防护', 8, 2, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (91, '正版图书', 9, 2, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (92, '文具纸品', 9, 2, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (93, '影音唱片', 9, 2, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (94, '游戏周边', 9, 2, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (95, '手办潮玩', 9, 2, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (101, '手机壳膜', 10, 2, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (102, '充电设备', 10, 2, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (103, '耳机音箱', 10, 2, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (104, '数据线材', 10, 2, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (105, '手机支架', 10, 2, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (111, '苹果手机', 11, 3, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (112, '华为手机', 11, 3, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (113, '小米手机', 11, 3, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (114, 'OPPO/vivo', 11, 3, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (115, '三星手机', 11, 3, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (121, '游戏本', 12, 3, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (122, '轻薄本', 12, 3, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (123, '台式整机', 12, 3, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (124, '一体机', 12, 3, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (125, '工作站', 12, 3, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (221, '连衣裙', 22, 3, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (222, 'T恤衬衫', 22, 3, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (223, '裤子半身裙', 22, 3, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (224, '卫衣毛衣', 22, 3, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (225, '外套大衣', 22, 3, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (311, '水乳套装', 31, 3, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (312, '精华原液', 31, 3, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (313, '面霜眼霜', 31, 3, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (314, '面膜泥膜', 31, 3, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (315, '洁面卸妆', 31, 3, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (431, '饼干糕点', 43, 3, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (432, '肉干肉脯', 43, 3, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (433, '糖果巧克力', 43, 3, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (434, '膨化零食', 43, 3, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (435, '坚果果干', 43, 3, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (521, '洗衣液洗衣粉', 52, 3, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (522, '洗洁精油污净', 52, 3, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (523, '拖把扫帚', 52, 3, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (524, '纸巾湿巾', 52, 3, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (525, '垃圾袋清洁刷', 52, 3, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (611, '一段奶粉', 61, 3, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (612, '二段奶粉', 61, 3, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (613, '三段奶粉', 61, 3, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (614, '米粉米糊', 61, 3, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (615, '果泥菜泥', 61, 3, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (721, '哑铃杠铃', 72, 3, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (722, '瑜伽垫瑜伽球', 72, 3, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (723, '筋膜枪', 72, 3, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (724, '跑步机', 72, 3, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (725, '握力器拉力带', 72, 3, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (811, '车载香薰', 81, 3, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (812, '座椅坐垫', 81, 3, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (813, '方向盘套', 81, 3, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (814, '车载收纳盒', 81, 3, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (815, '车内摆件', 81, 3, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (951, '盲盒手办', 95, 3, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (952, '动漫模型', 95, 3, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (953, '积木拼装', 95, 3, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (954, 'BJD娃娃', 95, 3, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (955, '周边徽章立牌', 95, 3, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (1021, '快充充电器', 102, 3, 1, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (1022, '无线充电宝', 102, 3, 2, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (1023, '车载快充头', 102, 3, 3, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (1024, '磁吸充电器', 102, 3, 4, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);
INSERT INTO `mall_product_category` VALUES (1025, '多口插排', 102, 3, 5, 1, NULL, '2026-07-27 10:33:57', '2026-07-27 10:33:57', 0);

-- ----------------------------
-- Table structure for mall_product_category_rel
-- ----------------------------
DROP TABLE IF EXISTS `mall_product_category_rel`;
CREATE TABLE `mall_product_category_rel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `category_id` bigint NOT NULL COMMENT '类目ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2081585630251085827 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品-类目关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_product_category_rel
-- ----------------------------
INSERT INTO `mall_product_category_rel` VALUES (2081585630230114305, 1, 121);
INSERT INTO `mall_product_category_rel` VALUES (2081585630230114306, 1, 122);
INSERT INTO `mall_product_category_rel` VALUES (2081585630230114307, 1, 123);
INSERT INTO `mall_product_category_rel` VALUES (2081585630251085825, 1, 124);
INSERT INTO `mall_product_category_rel` VALUES (2081585630251085826, 1, 125);
INSERT INTO `mall_product_category_rel` VALUES (2081990346806206466, 2, 221);
INSERT INTO `mall_product_category_rel` VALUES (2081990346806206467, 2, 222);
INSERT INTO `mall_product_category_rel` VALUES (2081990346806206468, 2, 223);
INSERT INTO `mall_product_category_rel` VALUES (2081990346806206469, 2, 224);
INSERT INTO `mall_product_category_rel` VALUES (2081990346806206470, 2, 225);
INSERT INTO `mall_product_category_rel` VALUES (2081990872604155906, 3, 94);

-- ----------------------------
-- Table structure for mall_refund
-- ----------------------------
DROP TABLE IF EXISTS `mall_refund`;
CREATE TABLE `mall_refund`  (
  `id` bigint NOT NULL COMMENT '退款单ID',
  `after_sale_id` bigint NOT NULL COMMENT '关联售后单ID',
  `order_id` bigint NOT NULL COMMENT '关联订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `refund_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '退款单号',
  `refund_amount` decimal(10, 2) NOT NULL COMMENT '退款金额',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '退款状态',
  `pay_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付方式',
  `transaction_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '第三方退款流水号',
  `fail_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '失败原因',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_after_sale`(`after_sale_id` ASC) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '退款表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_refund
-- ----------------------------

-- ----------------------------
-- Table structure for mall_resource_rel
-- ----------------------------
DROP TABLE IF EXISTS `mall_resource_rel`;
CREATE TABLE `mall_resource_rel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `resource_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源类型：product商品 / sku规格 / avatar头像 / after_sale售后 / chat_msg聊天',
  `resource_id` bigint NOT NULL COMMENT '资源记录ID',
  `file_id` bigint NOT NULL COMMENT '附件ID',
  `usage_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用途标识：main_image主图 / detail_image详情图 / video视频 / receipt凭证',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序序号',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_resource`(`resource_type` ASC, `resource_id` ASC) USING BTREE,
  INDEX `idx_file_id`(`file_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通用资源关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_resource_rel
-- ----------------------------
INSERT INTO `mall_resource_rel` VALUES (1, 'sku', 1, 2, 'main_image', 0, '2026-07-27 11:41:03');
INSERT INTO `mall_resource_rel` VALUES (2, 'sku', 1, 2, 'detail_image', 0, '2026-07-27 11:41:03');
INSERT INTO `mall_resource_rel` VALUES (3, 'sku', 2, 3, 'main_image', 0, '2026-07-27 11:41:03');
INSERT INTO `mall_resource_rel` VALUES (4, 'sku', 2, 3, 'detail_image', 0, '2026-07-27 11:41:03');
INSERT INTO `mall_resource_rel` VALUES (5, 'sku', 3, 4, 'main_image', 0, '2026-07-27 11:41:03');
INSERT INTO `mall_resource_rel` VALUES (6, 'sku', 3, 4, 'detail_image', 0, '2026-07-27 11:41:03');
INSERT INTO `mall_resource_rel` VALUES (7, 'sku', 4, 6, 'main_image', 0, '2026-07-28 14:29:15');
INSERT INTO `mall_resource_rel` VALUES (8, 'sku', 4, 6, 'detail_image', 0, '2026-07-28 14:29:15');
INSERT INTO `mall_resource_rel` VALUES (9, 'sku', 5, 6, 'main_image', 0, '2026-07-28 14:29:15');
INSERT INTO `mall_resource_rel` VALUES (10, 'sku', 5, 6, 'detail_image', 0, '2026-07-28 14:29:15');
INSERT INTO `mall_resource_rel` VALUES (11, 'sku', 6, 5, 'main_image', 0, '2026-07-28 14:29:15');
INSERT INTO `mall_resource_rel` VALUES (12, 'sku', 6, 5, 'detail_image', 0, '2026-07-28 14:29:15');
INSERT INTO `mall_resource_rel` VALUES (13, 'sku', 6, 7, 'detail_image', 1, '2026-07-28 14:29:15');
INSERT INTO `mall_resource_rel` VALUES (14, 'sku', 7, 8, 'main_image', 0, '2026-07-28 14:31:20');
INSERT INTO `mall_resource_rel` VALUES (15, 'sku', 7, 8, 'detail_image', 0, '2026-07-28 14:31:20');
INSERT INTO `mall_resource_rel` VALUES (16, 'sku', 8, 9, 'main_image', 0, '2026-07-28 14:31:20');
INSERT INTO `mall_resource_rel` VALUES (17, 'sku', 8, 9, 'detail_image', 0, '2026-07-28 14:31:20');
INSERT INTO `mall_resource_rel` VALUES (18, 'sku', 9, 10, 'main_image', 0, '2026-07-28 14:31:20');
INSERT INTO `mall_resource_rel` VALUES (19, 'sku', 9, 10, 'detail_image', 0, '2026-07-28 14:31:20');

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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品SKU表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_sku
-- ----------------------------
INSERT INTO `mall_sku` VALUES (1, 1, 5000.00, 98, 0, '{\"name\": \"规格1\", \"fileId\": 2}', '', 1, '2026-07-27 11:41:03', '2026-07-28 14:32:57');
INSERT INTO `mall_sku` VALUES (2, 1, 6000.00, 98, 0, '{\"name\": \"规格2\", \"fileId\": 3}', '', 1, '2026-07-27 11:41:03', '2026-07-28 14:32:57');
INSERT INTO `mall_sku` VALUES (3, 1, 7000.00, 98, 0, '{\"name\": \"规格3\", \"fileId\": 4}', '', 1, '2026-07-27 11:41:03', '2026-07-28 14:32:57');
INSERT INTO `mall_sku` VALUES (4, 2, 22.00, 98, 0, '{\"name\": \"1\", \"fileId\": 6}', '', 1, '2026-07-28 14:29:15', '2026-07-28 14:32:57');
INSERT INTO `mall_sku` VALUES (5, 2, 33.00, 98, 0, '{\"name\": \"2\", \"fileId\": 6}', '', 1, '2026-07-28 14:29:15', '2026-07-28 14:32:57');
INSERT INTO `mall_sku` VALUES (6, 2, 44.00, 98, 0, '{\"name\": \"3\", \"fileId\": 5}', '', 1, '2026-07-28 14:29:15', '2026-07-28 14:32:57');
INSERT INTO `mall_sku` VALUES (7, 3, 11.00, 98, 0, '{\"name\": \"1\", \"fileId\": 8}', '', 1, '2026-07-28 14:31:20', '2026-07-28 14:32:57');
INSERT INTO `mall_sku` VALUES (8, 3, 22.00, 98, 0, '{\"name\": \"2\", \"fileId\": 9}', '', 1, '2026-07-28 14:31:20', '2026-07-28 14:32:57');
INSERT INTO `mall_sku` VALUES (9, 3, 33.00, 98, 0, '{\"name\": \"3\", \"fileId\": 10}', '', 1, '2026-07-28 14:31:20', '2026-07-28 14:32:57');

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
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户地址表（版本化）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mall_user_address
-- ----------------------------
INSERT INTO `mall_user_address` VALUES (1, 2046463574828482561, '张三', '13800000001', '广东省', '深圳市', '南山区', '科技园科苑路15号', 0, '2026-04-26 22:00:00', '2026-04-27 14:18:31', 0);
INSERT INTO `mall_user_address` VALUES (2, 2046463574828482561, '李四', '13900000002', '广东省', '广州市', '天河区', '珠江新城花城大道88号', 0, '2026-04-26 22:05:00', '2026-05-06 01:08:50', 0);
INSERT INTO `mall_user_address` VALUES (3, 2046463574828482561, 'y', '19987665633', '浙江省', '嘉兴市', '嘉善县', '宇智波幼儿园', 1, '2026-04-27 14:27:16', '2026-05-06 01:08:51', 0);
INSERT INTO `mall_user_address` VALUES (4, 2046463574828482561, 'yyy', '16674532231', '浙江省', '嘉兴市', '嘉善县', '浙江省嘉兴市嘉善县', 0, '2026-04-29 14:37:41', '2026-05-06 01:08:49', 0);
INSERT INTO `mall_user_address` VALUES (5, 2046495738450235393, 'User', '17998556533', '北京市', '北京市', '昌平区', '高教大楼', 0, '2026-07-23 11:47:49', '2026-07-23 11:47:49', 0);

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
INSERT INTO `sys_user` VALUES (2046495738450235393, 'test', '$2a$10$81x9rMP5EKA0xP4zO2/jSuM9OyuUKQSXWHZ.bMdjTpy7Qnki0MlFe', '用户2', NULL, 'user', '/analytics', NULL, 1, '2026-04-21 15:46:21', '2026-07-28 14:30:14', 0);
INSERT INTO `sys_user` VALUES (2047131275460681729, 'user', '$2a$10$PKAE5Cd4tkv.hAEHDLlcd.oW5S4B8fYHkj/brPisHKWaF6WaVQgFq', '用户1', NULL, 'user', '/analytics', NULL, 1, '2026-04-23 09:51:45', '2026-07-28 14:30:15', 0);

SET FOREIGN_KEY_CHECKS = 1;
