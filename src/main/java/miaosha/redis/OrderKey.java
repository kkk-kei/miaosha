package miaosha.redis;

public class OrderKey extends BasePrefix{

    public OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getByUidGid = new OrderKey("ug");
}
