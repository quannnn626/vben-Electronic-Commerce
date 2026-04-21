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

 Date: 21/04/2026 15:51:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mall_order
-- ----------------------------
DROP TABLE IF EXISTS `mall_order`;
CREATE TABLE `mall_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单编号（业务唯一）',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `status` tinyint NOT NULL COMMENT '订单状态：0待支付 1已支付 2已取消 3已完成',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mall_order
-- ----------------------------

-- ----------------------------
-- Table structure for mall_order_item
-- ----------------------------
DROP TABLE IF EXISTS `mall_order_item`;
CREATE TABLE `mall_order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单明细ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL COMMENT '购买数量',
  `price` decimal(10, 2) NOT NULL COMMENT '下单时单价（价格快照）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mall_order_item
-- ----------------------------

-- ----------------------------
-- Table structure for mall_product
-- ----------------------------
DROP TABLE IF EXISTS `mall_product`;
CREATE TABLE `mall_product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品名称',
  `price` decimal(10, 2) NOT NULL COMMENT '商品单价',
  `stock` int NOT NULL COMMENT '库存数量',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '商品描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1上架 0下架',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mall_product
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (2046463574828482561, 'vben', '$2a$10$vj1MurvaDi6sMAycJOA8geMyUVdH.smxVC43ZF9idADeVE//FiV3e', 'Super', '', 'admin', '/analytics', '', 1, '2026-04-19 19:06:22', '2026-04-21 13:39:11', 0);
INSERT INTO `sys_user` VALUES (2046495738450235393, 'test', '$2a$10$81x9rMP5EKA0xP4zO2/jSuM9OyuUKQSXWHZ.bMdjTpy7Qnki0MlFe', NULL, NULL, 'user', '/analytics', NULL, 1, '2026-04-21 15:46:21', '2026-04-21 15:48:08', 0);

SET FOREIGN_KEY_CHECKS = 1;
