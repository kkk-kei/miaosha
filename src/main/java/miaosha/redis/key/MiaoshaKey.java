package miaosha.redis.key;

import miaosha.redis.BasePrefix;

public class MiaoshaKey extends BasePrefix {

    public MiaoshaKey(int expireSeconds,String prefix) {
        super(expireSeconds,prefix);
    }

    public static MiaoshaKey isStockEmptyByGid = new MiaoshaKey(0,"gi");
    public static MiaoshaKey getMiaoshaPathByUidGid = new MiaoshaKey(60,"mp");
}
