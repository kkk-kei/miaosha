package miaosha.redis.key;

import miaosha.redis.BasePrefix;

public class AccessKey extends BasePrefix {

    public AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static AccessKey withExpire(int expireSeconds){
        return new AccessKey(expireSeconds,"ac");
    }
}
