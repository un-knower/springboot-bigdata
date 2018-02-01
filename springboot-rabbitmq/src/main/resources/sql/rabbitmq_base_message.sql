/*
Navicat MySQL Data Transfer

Source Server         : 深圳阿里云
Source Server Version : 50173
Source Host           : 47.104.18.57:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50173
File Encoding         : 65001

Date: 2018-02-01 17:23:40
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for rabbitmq_base_message
-- ----------------------------
DROP TABLE IF EXISTS `rabbitmq_base_message`;
CREATE TABLE `rabbitmq_base_message` (
  `message_id` varchar(100) NOT NULL,
  `message_body` text,
  `consumer_queue` varchar(100) DEFAULT NULL,
  `send_nums` int(10) DEFAULT '1',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `is_dead` varchar(5) DEFAULT NULL,
  `status` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`message_id`),
  KEY `messageIdKey` (`message_id`) USING HASH
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
