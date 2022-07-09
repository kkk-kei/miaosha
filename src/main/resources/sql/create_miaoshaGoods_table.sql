CREATE TABLE `miaosha_goods` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀的商品表',
    `goods_id` bigint(20) DEFAULT NULL COMMENT '商品ID',
    `miaosha_price` decimal(10,2) DEFAULT '0.00' COMMENT '秒杀价',
    `stock_count` int(11) DEFAULT NULL COMMENT '库存数量',
    `start_date` datetime DEFAULT NULL COMMENT '秒杀开始时间',
    `end_date` datetime DEFAULT NULL COMMENT '秒杀结束时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

INSERT INTO `miaosha_goods` VALUES
    (1,1,0.01,9,'2022-07-09 21:51:23','2022-07-09 21:51:27'),
    (2,2,0.01,9,'2022-07-09 21:40:14','2022-07-09 14:00:24'),
    (3,3,0.01,9,'2022-07-09 21:40:14','2022-07-09 14:00:24'),
    (4,4,0.01,9,'2022-07-09 21:40:14','2022-07-09 14:00:24');