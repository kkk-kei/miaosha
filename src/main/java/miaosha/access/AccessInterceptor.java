package miaosha.access;

import com.alibaba.fastjson.JSON;
import miaosha.domain.MiaoshaUser;
import miaosha.redis.key.AccessKey;
import miaosha.redis.RedisService;
import miaosha.result.CodeMsg;
import miaosha.result.Result;
import miaosha.service.MiaoshaUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    RedisService redisService;

    @Autowired
    MiaoshaUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            //优先成本低的操作
            Filter filter = handlerMethod.getMethodAnnotation(Filter.class);
            if(filter!=null){
                //随机数过滤
                boolean pass = new Random().nextBoolean();
                if(!pass){
                    render(response,CodeMsg.MIAOSHA_REFUSE);
                    return false;
                }
            }

            NeedLogin needLogin = handlerMethod.getMethodAnnotation(NeedLogin.class);
            if(needLogin!=null){
                if(needLogin==null){
                    return true;
                }
                boolean login = needLogin.value();
                if(login){
                    MiaoshaUser user = getMiaoUser(request,response);
                    if(user==null){
                        render(response,CodeMsg.SESSION_ERROR);
                        return false;
                    }
                    UserContext.set(user);
                }
            }

            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if(accessLimit!=null){
                String key = request.getRequestURI();
                int seconds = accessLimit.seconds();
                int maxCount = accessLimit.maxCount();
                Integer count = redisService.get(AccessKey.withExpire(seconds), key, Integer.class);
                if(count == null){
                    redisService.set(AccessKey.withExpire(seconds),key,1);
                }else if(count < maxCount){
                    redisService.incr(AccessKey.withExpire(seconds),key);
                }else{
                    render(response,CodeMsg.REQUEST_ILLEGAL);
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    private void render(HttpServletResponse response, CodeMsg codeMsg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str  = JSON.toJSONString(Result.error(codeMsg));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    private MiaoshaUser getMiaoUser(HttpServletRequest request,HttpServletResponse response) {
        //通过参数传递token（优先处理）
        String parameterToken = request.getParameter(MiaoshaUserService.COOKIE_NAME_TOKEN);
        //通过cookie传递token
        Cookie[] cookies = request.getCookies();
        String cookieToken = getCookieValue(cookies,MiaoshaUserService.COOKIE_NAME_TOKEN);
        if(StringUtils.isEmpty(parameterToken)&&StringUtils.isEmpty(cookieToken)){
            return null;
        }
        String token = StringUtils.isEmpty(parameterToken)?cookieToken:parameterToken;
        return userService.getByToken(response,token);
    }

    private String getCookieValue(Cookie[] cookies, String cookieName) {
        if(cookies==null||cookies.length<=0){
            return null;
        }
        for (Cookie cookie : cookies) {
            if(cookieName.equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }
}
