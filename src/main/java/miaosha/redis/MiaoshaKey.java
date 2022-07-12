package miaosha.redis;

public class MiaoshaKey extends BasePrefix{

    public MiaoshaKey(String prefix) {
        super(prefix);
    }

    public static MiaoshaKey isStockEmptyByGid = new MiaoshaKey("gi");
}
