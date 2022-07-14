package miaosha.redis.key;

import miaosha.redis.BasePrefix;

public class GoodsKey extends BasePrefix {

    public GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodsKey getGoodsList = new GoodsKey(60,"gl");
    public static GoodsKey getGoodsDetailByGid = new GoodsKey(60,"gd");
    public static GoodsKey getGoodsStockByGid = new GoodsKey(0,"gs");
}
