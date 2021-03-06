package miaosha.service;

import miaosha.dao.MiaoshaUserDao;
import miaosha.domain.MiaoshaUser;
import miaosha.exception.GlobalException;
import miaosha.redis.key.MiaoshaUserKey;
import miaosha.redis.RedisService;
import miaosha.result.CodeMsg;
import miaosha.util.MD5Util;
import miaosha.util.UUIDUtil;
import miaosha.vo.LoginVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {
    @Autowired
    MiaoshaUserDao miaoshaUserDao;
    @Autowired
    RedisService redisService;

    public static final String COOKIE_NAME_TOKEN = "token";

    public MiaoshaUser getByID(long userID){
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getByID, "" + userID, MiaoshaUser.class);
        if(user==null){
            user = miaoshaUserDao.getById(userID);
            if(user==null){
                return user;
            }
            redisService.set(MiaoshaUserKey.getByID,""+userID,user);
        }
        return user;
    }

    public boolean login(HttpServletResponse response, LoginVO loginVO) {
        if(loginVO==null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVO.getMobile();
        String formPWD = loginVO.getPassword();
        MiaoshaUser user = getByID(Long.parseLong(mobile));
        if(user==null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        String dbPWD = user.getPassword();
        String randomSalt = user.getSalt();
        String calPWD = MD5Util.formPwdToDBPwd(formPWD, randomSalt);
        if(!dbPWD.equals(calPWD)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        String token = UUIDUtil.uuid();
        addCookie(response,token,user);
        return true;
    }

    private void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
        redisService.set(MiaoshaUserKey.getByToken,token,user);
        Cookie cookie = new Cookie("token",token);
        cookie.setMaxAge(MiaoshaUserKey.TOKEN_EXPIRE);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public MiaoshaUser getByToken(HttpServletResponse response,String token) {
        if(StringUtils.isEmpty(token)){
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getByToken, token, MiaoshaUser.class);
        if(user!=null){
            addCookie(response,token,user);
        }
        return user;
    }
}
