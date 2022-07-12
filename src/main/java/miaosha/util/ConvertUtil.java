package miaosha.util;

import com.alibaba.fastjson.JSON;

import java.util.List;

public class ConvertUtil {

    public static <T> T stringToBean(String value,Class<T> clazz){
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
    public static <T> String beanToString(T value){
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

}
