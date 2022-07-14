package miaosha.redis.key;

import miaosha.redis.BasePrefix;

public class OrderKey extends BasePrefix {

    public OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("ug");
    public static OrderKey getOrderInfoByID = new OrderKey("oi");
}
