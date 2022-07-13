package miaosha.access;

import miaosha.domain.MiaoshaUser;
import org.springframework.stereotype.Service;


@Service
public class UserContext {
    private static ThreadLocal<MiaoshaUser> threadLocal = new ThreadLocal<>();

    public static void set(MiaoshaUser user){
        threadLocal.set(user);
    }

    public static MiaoshaUser get(){
        return threadLocal.get();
    }
}
