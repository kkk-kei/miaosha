package miaosha.redis;

public class MiaoshaKey extends BasePrefix{

    public MiaoshaKey(int expireSeconds,String prefix) {
        super(expireSeconds,prefix);
    }

    public static MiaoshaKey isStockEmptyByGid = new MiaoshaKey(0,"gi");
    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60,"mp");
}
