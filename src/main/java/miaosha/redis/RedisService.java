package miaosha.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

@Service
public class RedisService {
    @Autowired
    JedisPool jedisPool;

    public <T> boolean set(KeyPrefix prefix,String key,T value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String val = beanToString(value);
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
    public <T> T get(KeyPrefix prefix,String key,Class<T> clazz){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+key;
            String val = jedis.get(realKey);
            return (T)stringToBean(val, clazz);
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

    private <T> T stringToBean(String value,Class<T> clazz){
        if(value==null||value.length()<=0||clazz==null){
            return null;
        }
        if(clazz==int.class||clazz==Integer.class){
            return (T) Integer.valueOf(value);
        }else if(clazz==long.class||clazz==Long.class){
            return (T) Long.valueOf(value);
        }else if(clazz==String.class){
            return (T) value;
        }else if(clazz== List.class){
            return JSON.parseObject(value,clazz);
        }
        return JSON.toJavaObject(JSON.parseObject(value),clazz);
    }
    private <T> String beanToString(T value){
        if(value==null){
            return null;
        }
        Class<?> clazz = value.getClass();
        if(clazz == int.class||clazz == Integer.class){
            return ""+value;
        }else if(clazz == long.class||clazz == Long.class){
            return ""+value;
        }else if(clazz == String.class){
            return (String) value;
        }
        return JSON.toJSONString(value);
    }
    private void returnToPool(Jedis jedis){
        if(jedis!=null){
            jedis.close();
        }
    }

}
