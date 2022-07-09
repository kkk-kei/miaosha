CREATE TABLE `miaosha_order` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
    `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
    `goods_id` bigint(20) DEFAULT NULL COMMENT '商品ID',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;