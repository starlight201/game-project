/*
 Navicat Premium Dump SQL

 Source Server         : server1
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : 192.168.10.100:3306
 Source Schema         : redpacket_game

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 18/12/2025 16:45:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for attendance_records
-- ----------------------------
DROP TABLE IF EXISTS `attendance_records`;
CREATE TABLE `attendance_records`  (
  `record_id` int NOT NULL AUTO_INCREMENT,
  `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `student_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `attendance_status` enum('出勤','请假','旷课','迟到') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `call_time` datetime NOT NULL,
  `leave_note` tinyint(1) NULL DEFAULT 0,
  `session_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `student_id`(`student_id` ASC) USING BTREE,
  INDEX `session_id`(`session_id` ASC) USING BTREE,
  CONSTRAINT `attendance_records_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `attendance_records_ibfk_2` FOREIGN KEY (`session_id`) REFERENCES `roll_call_sessions` (`session_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 71 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of attendance_records
-- ----------------------------
INSERT INTO `attendance_records` VALUES (11, '2021002', '李四', '出勤', '2025-12-11 00:19:29', 0, 3);
INSERT INTO `attendance_records` VALUES (12, '2021005', '钱七', '请假', '2025-12-11 00:19:32', 0, 3);
INSERT INTO `attendance_records` VALUES (13, '2021003', '王五', '旷课', '2025-12-11 00:19:34', 0, 3);
INSERT INTO `attendance_records` VALUES (14, '2021004', '赵六', '迟到', '2025-12-11 00:19:36', 0, 3);
INSERT INTO `attendance_records` VALUES (15, '2021001', '张三', '出勤', '2025-12-11 00:19:38', 0, 3);
INSERT INTO `attendance_records` VALUES (16, '2021003', '王五', '出勤', '2025-12-11 00:29:41', 0, 4);
INSERT INTO `attendance_records` VALUES (17, '2021001', '张三', '出勤', '2025-12-11 00:29:42', 0, 4);
INSERT INTO `attendance_records` VALUES (18, '2021002', '李四', '请假', '2025-12-11 00:29:44', 0, 4);
INSERT INTO `attendance_records` VALUES (34, '2021003', '王五', '出勤', '2025-12-18 14:35:38', 0, 5);
INSERT INTO `attendance_records` VALUES (35, '2021004', '赵六', '出勤', '2025-12-18 14:35:40', 0, 5);
INSERT INTO `attendance_records` VALUES (36, '2021002', '李四', '出勤', '2025-12-18 14:35:41', 0, 5);
INSERT INTO `attendance_records` VALUES (37, '2021001', '张三', '请假', '2025-12-18 14:35:43', 0, 5);
INSERT INTO `attendance_records` VALUES (38, '2021005', '钱七', '请假', '2025-12-18 14:35:45', 0, 5);
INSERT INTO `attendance_records` VALUES (39, '2021001', '张三', '出勤', '2025-12-18 15:08:10', 0, 6);
INSERT INTO `attendance_records` VALUES (40, '2021002', '李四', '出勤', '2025-12-18 15:08:12', 0, 6);
INSERT INTO `attendance_records` VALUES (41, '2021003', '王五', '出勤', '2025-12-18 15:08:14', 0, 6);
INSERT INTO `attendance_records` VALUES (42, '2021004', '赵六', '请假', '2025-12-18 15:08:16', 0, 6);
INSERT INTO `attendance_records` VALUES (43, '2021005', '钱七', '迟到', '2025-12-18 15:08:18', 0, 6);
INSERT INTO `attendance_records` VALUES (44, '2021001', '张三', '出勤', '2025-12-18 15:26:12', 0, 7);
INSERT INTO `attendance_records` VALUES (45, '2021002', '李四', '出勤', '2025-12-18 15:26:14', 0, 7);
INSERT INTO `attendance_records` VALUES (46, '2021003', '王五', '出勤', '2025-12-18 15:26:16', 0, 7);
INSERT INTO `attendance_records` VALUES (47, '2021004', '赵六', '出勤', '2025-12-18 15:26:18', 0, 7);
INSERT INTO `attendance_records` VALUES (48, '2021005', '钱七', '出勤', '2025-12-18 15:26:20', 0, 7);
INSERT INTO `attendance_records` VALUES (49, '2021005', '钱七', '出勤', '2025-12-18 15:27:44', 0, 10);
INSERT INTO `attendance_records` VALUES (50, '2021003', '王五', '出勤', '2025-12-18 15:27:46', 0, 10);
INSERT INTO `attendance_records` VALUES (51, '2021001', '张三', '出勤', '2025-12-18 15:36:09', 0, 11);
INSERT INTO `attendance_records` VALUES (52, '2021002', '李四', '请假', '2025-12-18 15:36:11', 0, 11);
INSERT INTO `attendance_records` VALUES (53, '2021003', '王五', '旷课', '2025-12-18 15:36:13', 0, 11);
INSERT INTO `attendance_records` VALUES (54, '2021004', '赵六', '迟到', '2025-12-18 15:36:15', 0, 11);
INSERT INTO `attendance_records` VALUES (55, '2021005', '钱七', '出勤', '2025-12-18 15:36:17', 0, 11);
INSERT INTO `attendance_records` VALUES (56, '2021006', '孙八', '请假', '2025-12-18 15:36:19', 0, 11);
INSERT INTO `attendance_records` VALUES (57, '2021007', '周九', '旷课', '2025-12-18 15:36:21', 0, 11);
INSERT INTO `attendance_records` VALUES (58, '2021008', '吴十', '出勤', '2025-12-18 15:36:23', 0, 11);
INSERT INTO `attendance_records` VALUES (59, '2021009', '郑一', '请假', '2025-12-18 15:36:25', 0, 11);
INSERT INTO `attendance_records` VALUES (60, '2021010', '王二', '出勤', '2025-12-18 15:36:27', 0, 11);
INSERT INTO `attendance_records` VALUES (61, '2021011', '冯三', '出勤', '2025-12-18 15:36:29', 0, 11);
INSERT INTO `attendance_records` VALUES (62, '2021012', '陈四', '出勤', '2025-12-18 15:36:31', 0, 11);
INSERT INTO `attendance_records` VALUES (63, '2021013', '褚五', '出勤', '2025-12-18 15:36:33', 0, 11);
INSERT INTO `attendance_records` VALUES (64, '2021014', '卫六', '出勤', '2025-12-18 15:36:35', 0, 11);
INSERT INTO `attendance_records` VALUES (65, '2021015', '蒋七', '出勤', '2025-12-18 15:36:37', 0, 11);
INSERT INTO `attendance_records` VALUES (66, '2021016', '沈八', '出勤', '2025-12-18 15:36:39', 0, 11);
INSERT INTO `attendance_records` VALUES (67, '2021017', '韩九', '出勤', '2025-12-18 15:37:03', 0, 11);
INSERT INTO `attendance_records` VALUES (68, '2021018', '杨十', '出勤', '2025-12-18 15:37:05', 0, 11);
INSERT INTO `attendance_records` VALUES (69, '2021019', '朱一', '出勤', '2025-12-18 15:37:07', 0, 11);
INSERT INTO `attendance_records` VALUES (70, '2021020', '秦二', '出勤', '2025-12-18 15:37:09', 0, 11);

-- ----------------------------
-- Table structure for outfit_items
-- ----------------------------
DROP TABLE IF EXISTS `outfit_items`;
CREATE TABLE `outfit_items`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `outfit_id` int NOT NULL,
  `item_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `outfit_id`(`outfit_id` ASC) USING BTREE,
  CONSTRAINT `outfit_items_ibfk_1` FOREIGN KEY (`outfit_id`) REFERENCES `outfits` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of outfit_items
-- ----------------------------

-- ----------------------------
-- Table structure for outfits
-- ----------------------------
DROP TABLE IF EXISTS `outfits`;
CREATE TABLE `outfits`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `outfit_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `outfits_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of outfits
-- ----------------------------

-- ----------------------------
-- Table structure for owned_items
-- ----------------------------
DROP TABLE IF EXISTS `owned_items`;
CREATE TABLE `owned_items`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `item_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `owned_items_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of owned_items
-- ----------------------------

-- ----------------------------
-- Table structure for roll_call_sessions
-- ----------------------------
DROP TABLE IF EXISTS `roll_call_sessions`;
CREATE TABLE `roll_call_sessions`  (
  `session_id` int NOT NULL AUTO_INCREMENT,
  `session_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NULL DEFAULT NULL,
  `call_type` enum('全点','抽点') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `strategy` enum('随机选取','优先旷课多','优先点到少') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `student_count` int NULL DEFAULT NULL,
  `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`session_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of roll_call_sessions
-- ----------------------------
INSERT INTO `roll_call_sessions` VALUES (1, '点名-Thu Dec 11 00:15:23 GMT+08:00 2025', '2025-12-11 00:15:23', NULL, '全点', '随机选取', 5, '2025-12-11 00:15:23');
INSERT INTO `roll_call_sessions` VALUES (2, '点名-Thu Dec 11 00:16:03 GMT+08:00 2025', '2025-12-11 00:16:03', NULL, '全点', '随机选取', 5, '2025-12-11 00:16:03');
INSERT INTO `roll_call_sessions` VALUES (3, '点名-Thu Dec 11 00:19:25 GMT+08:00 2025', '2025-12-11 00:19:25', '2025-12-11 00:19:38', '全点', '随机选取', 5, '2025-12-11 00:19:25');
INSERT INTO `roll_call_sessions` VALUES (4, '点名-Thu Dec 11 00:29:35 GMT+08:00 2025', '2025-12-11 00:29:35', '2025-12-11 00:29:44', '抽点', '优先旷课多', 3, '2025-12-11 00:29:35');
INSERT INTO `roll_call_sessions` VALUES (5, '点名-Thu Dec 18 14:35:35 GMT+08:00 2025', '2025-12-18 14:35:35', '2025-12-18 14:35:46', '全点', '随机选取', 5, '2025-12-18 14:35:35');
INSERT INTO `roll_call_sessions` VALUES (6, '点名-Thu Dec 18 15:11:59 GMT+08:00 2025', '2025-12-18 15:08:07', '2025-12-18 15:08:19', '全点', '随机选取', 5, '2025-12-18 15:08:07');
INSERT INTO `roll_call_sessions` VALUES (7, '点名-Thu Dec 18 15:26:09 GMT+08:00 2025', '2025-12-18 15:26:09', '2025-12-18 15:26:20', '全点', '随机选取', 5, '2025-12-18 15:26:09');
INSERT INTO `roll_call_sessions` VALUES (8, '点名-Thu Dec 18 15:26:49 GMT+08:00 2025', '2025-12-18 15:26:49', NULL, '抽点', '随机选取', 1, '2025-12-18 15:26:49');
INSERT INTO `roll_call_sessions` VALUES (9, '点名-Thu Dec 18 15:27:05 GMT+08:00 2025', '2025-12-18 15:27:05', NULL, '抽点', '随机选取', 1, '2025-12-18 15:27:05');
INSERT INTO `roll_call_sessions` VALUES (10, '点名-Thu Dec 18 15:27:39 GMT+08:00 2025', '2025-12-18 15:27:39', '2025-12-18 15:27:46', '抽点', '随机选取', 2, '2025-12-18 15:27:39');
INSERT INTO `roll_call_sessions` VALUES (11, '点名-Thu Dec 18 15:36:07 GMT+08:00 2025', '2025-12-18 15:36:06', '2025-12-18 15:37:09', '全点', '随机选取', 20, '2025-12-18 15:36:06');

-- ----------------------------
-- Table structure for shop_items
-- ----------------------------
DROP TABLE IF EXISTS `shop_items`;
CREATE TABLE `shop_items`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `item_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `item_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `item_price` decimal(10, 2) NULL DEFAULT NULL,
  `style_points` int NULL DEFAULT NULL,
  `item_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `image_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `unlock_level` int NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `item_name`(`item_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 757 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shop_items
-- ----------------------------
INSERT INTO `shop_items` VALUES (1, '羊毛围巾', '温暖舒适', 90.00, 18, 'scarf', 'scarf_wool.png', 1);
INSERT INTO `shop_items` VALUES (2, '水手帽', '航海风格帽子', 80.00, 15, 'hat', 'hat_sailor.png', 1);
INSERT INTO `shop_items` VALUES (3, '圆框眼镜', '文艺复古', 60.00, 12, 'glasses', 'glasses_round.png', 1);
INSERT INTO `shop_items` VALUES (4, '条纹围巾', '经典条纹设计', 40.00, 8, 'scarf', 'scarf_stripe.png', 1);
INSERT INTO `shop_items` VALUES (5, '木质手杖', '经典木质', 120.00, 20, 'cane', 'cane_wood.png', 1);
INSERT INTO `shop_items` VALUES (6, 'VR眼镜', '科技感十足', 300.00, 40, 'glasses', 'glasses_vr.png', 1);
INSERT INTO `shop_items` VALUES (7, '丝绸围巾', '高雅华丽', 200.00, 35, 'scarf', 'scarf_silk.png', 1);
INSERT INTO `shop_items` VALUES (8, '魔法手杖', '闪闪发光', 800.00, 80, 'cane', 'cane_magic.png', 1);
INSERT INTO `shop_items` VALUES (9, '单片眼镜', '绅士风度', 150.00, 30, 'glasses', 'glasses_monocle.png', 1);
INSERT INTO `shop_items` VALUES (10, '波点领带', '活泼可爱', 45.00, 10, 'tie', 'tie_polka.png', 1);
INSERT INTO `shop_items` VALUES (11, '宝石手杖', '镶嵌宝石', 600.00, 60, 'cane', 'cane_gem.png', 1);
INSERT INTO `shop_items` VALUES (12, '生日帽', '派对必备', 30.00, 5, 'hat', 'hat_party.png', 1);
INSERT INTO `shop_items` VALUES (13, '经典鸭舌帽', '时尚鸭舌帽，彰显个性', 50.00, 10, 'hat', 'hat_classic.png', 1);
INSERT INTO `shop_items` VALUES (14, '金属手杖', '现代感强', 200.00, 30, 'cane', 'cane_metal.png', 1);
INSERT INTO `shop_items` VALUES (15, '王冠', '皇家尊贵', 500.00, 50, 'hat', 'hat_crown.png', 1);
INSERT INTO `shop_items` VALUES (16, '魔术师礼帽', '神秘的魔术师帽子', 120.00, 25, 'hat', 'hat_magician.png', 1);
INSERT INTO `shop_items` VALUES (17, '红色领带', '正式场合', 60.00, 12, 'tie', 'tie_red.png', 1);
INSERT INTO `shop_items` VALUES (18, '蝴蝶结', '优雅别致', 80.00, 18, 'tie', 'tie_bow.png', 1);
INSERT INTO `shop_items` VALUES (19, '格子围巾', '英伦风格', 70.00, 15, 'scarf', 'scarf_check.png', 1);
INSERT INTO `shop_items` VALUES (20, '墨镜', '酷炫有型', 100.00, 20, 'glasses', 'glasses_sunglasses.png', 1);
INSERT INTO `shop_items` VALUES (21, '金边领带', '奢华尊贵', 350.00, 45, 'tie', 'tie_gold.png', 1);

-- ----------------------------
-- Table structure for student_attendance_stats
-- ----------------------------
DROP TABLE IF EXISTS `student_attendance_stats`;
CREATE TABLE `student_attendance_stats`  (
  `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `total_attended` int NULL DEFAULT 0,
  `total_absent` int NULL DEFAULT 0,
  `total_leave` int NULL DEFAULT 0,
  `total_late` int NULL DEFAULT 0,
  PRIMARY KEY (`student_id`) USING BTREE,
  CONSTRAINT `student_attendance_stats_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_attendance_stats
-- ----------------------------
INSERT INTO `student_attendance_stats` VALUES ('2021001', 5, 0, 1, 0);
INSERT INTO `student_attendance_stats` VALUES ('2021002', 4, 0, 2, 0);
INSERT INTO `student_attendance_stats` VALUES ('2021003', 5, 2, 0, 0);
INSERT INTO `student_attendance_stats` VALUES ('2021004', 2, 0, 1, 2);
INSERT INTO `student_attendance_stats` VALUES ('2021005', 3, 0, 2, 1);
INSERT INTO `student_attendance_stats` VALUES ('2021006', 0, 0, 1, 0);
INSERT INTO `student_attendance_stats` VALUES ('2021007', 0, 1, 0, 0);
INSERT INTO `student_attendance_stats` VALUES ('2021008', 1, 0, 0, 0);
INSERT INTO `student_attendance_stats` VALUES ('2021009', 0, 0, 1, 0);
INSERT INTO `student_attendance_stats` VALUES ('2021010', 1, 0, 0, 0);
INSERT INTO `student_attendance_stats` VALUES ('2021011', 1, 0, 0, 0);
INSERT INTO `student_attendance_stats` VALUES ('2021012', 1, 0, 0, 0);
INSERT INTO `student_attendance_stats` VALUES ('2021013', 1, 0, 0, 0);
INSERT INTO `student_attendance_stats` VALUES ('2021014', 1, 0, 0, 0);
INSERT INTO `student_attendance_stats` VALUES ('2021015', 1, 0, 0, 0);
INSERT INTO `student_attendance_stats` VALUES ('2021016', 1, 0, 0, 0);
INSERT INTO `student_attendance_stats` VALUES ('2021017', 1, 0, 0, 0);
INSERT INTO `student_attendance_stats` VALUES ('2021018', 1, 0, 0, 0);
INSERT INTO `student_attendance_stats` VALUES ('2021019', 1, 0, 0, 0);
INSERT INTO `student_attendance_stats` VALUES ('2021020', 1, 0, 0, 0);

-- ----------------------------
-- Table structure for students
-- ----------------------------
DROP TABLE IF EXISTS `students`;
CREATE TABLE `students`  (
  `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `photo_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`student_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of students
-- ----------------------------
INSERT INTO `students` VALUES ('2021001', '张三', 'photos/zhangsan.jpg', '2025-12-11 00:15:02');
INSERT INTO `students` VALUES ('2021002', '李四', 'photos/lisi.jpg', '2025-12-11 00:15:02');
INSERT INTO `students` VALUES ('2021003', '王五', 'photos/wangwu.jpg', '2025-12-11 00:15:02');
INSERT INTO `students` VALUES ('2021004', '赵六', 'photos/zhaoliu.jpg', '2025-12-11 00:15:02');
INSERT INTO `students` VALUES ('2021005', '钱七', 'photos/qianqi.jpg', '2025-12-11 00:15:02');
INSERT INTO `students` VALUES ('2021006', '孙八', 'photos/sunba.jpg', '2025-12-11 00:15:02');
INSERT INTO `students` VALUES ('2021007', '周九', 'photos/zhoujiu.jpg', '2025-12-11 00:15:02');
INSERT INTO `students` VALUES ('2021008', '吴十', 'photos/wushi.jpg', '2025-12-11 00:15:02');
INSERT INTO `students` VALUES ('2021009', '郑一', 'photos/zhengyi.jpg', '2025-12-11 00:15:02');
INSERT INTO `students` VALUES ('2021010', '王二', 'photos/wanger.jpg', '2025-12-11 00:15:02');
INSERT INTO `students` VALUES ('2021011', '冯三', 'photos/fengsan.jpg', '2025-12-11 00:15:02');
INSERT INTO `students` VALUES ('2021012', '陈四', 'photos/chensi.jpg', '2025-12-11 00:15:02');
INSERT INTO `students` VALUES ('2021013', '褚五', 'photos/chuwu.jpg', '2025-12-11 00:15:02');
INSERT INTO `students` VALUES ('2021014', '卫六', 'photos/weiliu.jpg', '2025-12-11 00:15:02');
INSERT INTO `students` VALUES ('2021015', '蒋七', 'photos/jiangqi.jpg', '2025-12-11 00:15:02');
INSERT INTO `students` VALUES ('2021016', '沈八', 'photos/shenba.jpg', '2025-12-11 00:15:02');
INSERT INTO `students` VALUES ('2021017', '韩九', 'photos/hanjiu.jpg', '2025-12-11 00:15:02');
INSERT INTO `students` VALUES ('2021018', '杨十', 'photos/yangshi.jpg', '2025-12-11 00:15:02');
INSERT INTO `students` VALUES ('2021019', '朱一', 'photos/zhuyi.jpg', '2025-12-11 00:15:02');
INSERT INTO `students` VALUES ('2021020', '秦二', 'photos/qiner.jpg', '2025-12-11 00:15:02');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL,
  `total_money` double NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 2149.9389156345887);

-- ----------------------------
-- Table structure for user_current_outfit
-- ----------------------------
DROP TABLE IF EXISTS `user_current_outfit`;
CREATE TABLE `user_current_outfit`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NULL DEFAULT NULL,
  `item_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `item_order` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `user_current_outfit_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 122 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_current_outfit
-- ----------------------------
INSERT INTO `user_current_outfit` VALUES (117, 1, '宝石手杖', 0);
INSERT INTO `user_current_outfit` VALUES (118, 1, '经典鸭舌帽', 1);
INSERT INTO `user_current_outfit` VALUES (119, 1, '条纹围巾', 2);
INSERT INTO `user_current_outfit` VALUES (120, 1, '蝴蝶结', 3);
INSERT INTO `user_current_outfit` VALUES (121, 1, '圆框眼镜', 4);

-- ----------------------------
-- Table structure for user_levels
-- ----------------------------
DROP TABLE IF EXISTS `user_levels`;
CREATE TABLE `user_levels`  (
  `user_id` int NOT NULL,
  `level` int NULL DEFAULT 1,
  PRIMARY KEY (`user_id`) USING BTREE,
  CONSTRAINT `user_levels_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_levels
-- ----------------------------
INSERT INTO `user_levels` VALUES (1, 1);

-- ----------------------------
-- Table structure for user_owned_items
-- ----------------------------
DROP TABLE IF EXISTS `user_owned_items`;
CREATE TABLE `user_owned_items`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NULL DEFAULT NULL,
  `item_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `user_owned_items_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_owned_items
-- ----------------------------
INSERT INTO `user_owned_items` VALUES (22, 1, '宝石手杖');
INSERT INTO `user_owned_items` VALUES (23, 1, '经典鸭舌帽');
INSERT INTO `user_owned_items` VALUES (24, 1, '圆框眼镜');
INSERT INTO `user_owned_items` VALUES (25, 1, '条纹围巾');
INSERT INTO `user_owned_items` VALUES (26, 1, '木质手杖');
INSERT INTO `user_owned_items` VALUES (27, 1, '蝴蝶结');
INSERT INTO `user_owned_items` VALUES (28, 1, '红色领带');

-- ----------------------------
-- Table structure for user_saved_outfits
-- ----------------------------
DROP TABLE IF EXISTS `user_saved_outfits`;
CREATE TABLE `user_saved_outfits`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NULL DEFAULT NULL,
  `outfit_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `item_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `item_order` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `user_saved_outfits_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_saved_outfits
-- ----------------------------
INSERT INTO `user_saved_outfits` VALUES (2, 1, '222', '条纹围巾', 0);
INSERT INTO `user_saved_outfits` VALUES (3, 1, '333', '条纹围巾', 0);
INSERT INTO `user_saved_outfits` VALUES (4, 1, '444', '条纹围巾', 0);
INSERT INTO `user_saved_outfits` VALUES (5, 1, '444', '蝴蝶结', 1);
INSERT INTO `user_saved_outfits` VALUES (6, 1, '444', '经典鸭舌帽', 2);
INSERT INTO `user_saved_outfits` VALUES (7, 1, '444', '圆框眼镜', 3);
INSERT INTO `user_saved_outfits` VALUES (8, 1, '123', '宝石手杖', 0);
INSERT INTO `user_saved_outfits` VALUES (9, 1, '123', '蝴蝶结', 1);
INSERT INTO `user_saved_outfits` VALUES (10, 1, '123', '条纹围巾', 2);
INSERT INTO `user_saved_outfits` VALUES (11, 1, '123', '经典鸭舌帽', 3);
INSERT INTO `user_saved_outfits` VALUES (12, 1, '456', '宝石手杖', 0);
INSERT INTO `user_saved_outfits` VALUES (13, 1, '456', '经典鸭舌帽', 1);
INSERT INTO `user_saved_outfits` VALUES (14, 1, '456', '条纹围巾', 2);
INSERT INTO `user_saved_outfits` VALUES (15, 1, '456', '蝴蝶结', 3);
INSERT INTO `user_saved_outfits` VALUES (16, 1, '456', '圆框眼镜', 4);

SET FOREIGN_KEY_CHECKS = 1;
