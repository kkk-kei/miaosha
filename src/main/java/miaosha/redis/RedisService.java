package miaosha.redis;

import miaosha.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {
    @Autowired
    JedisPool jedisPool;

    public <T> T get(KeyPrefix prefix,String key,Class<T> clazz){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+key;
            String val = jedis.get(realKey);
            return (T)ConvertUtil.stringToBean(val, clazz);
        }finally {
            returnToPool(jedis);
        }
    }
    public <T> boolean set(KeyPrefix prefix,String key,T value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String val = ConvertUtil.beanToString(value);
            if(val==null||val.length()<=0){
                return false;
            }
            String realKey = prefix.getPrefix()+key;
            int seconds = prefix.expireSeconds();
            if(seconds<=0){
                jedis.set(realKey,val);
            }else{
                jedis.setex(realKey,seconds,val);
            }
        }finally {
            returnToPool(jedis);
        }
        return true;
    }
    public boolean delete(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+key;
            Long res = jedis.del(realKey);
            return res>0;
        }finally {
            returnToPool(jedis);
        }
    }
    public boolean exists(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+key;
            return jedis.exists(realKey);
        }finally {
            returnToPool(jedis);
        }
    }
    public Long incr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+key;
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }
    public Long decr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+key;
            return jedis.decr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    private void returnToPool(Jedis jedis){
        if(jedis!=null){
            jedis.close();
        }
    }

}
