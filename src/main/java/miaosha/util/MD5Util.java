package miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

public class MD5Util {

    public static final String salt = "1a2b3c4d";
    public static String md5(String value){
        return DigestUtils.md5Hex(value);
    }

    public static String inputPwdToFormPwd(String value){
        String val = ""+salt.charAt(0)+salt.charAt(1)+value+salt.charAt(2)+salt.charAt(3);
        return md5(val);
    }
    public static String formPwdToDBPwd(String value,String randomSalt){
        String val = ""+randomSalt.charAt(0)+randomSalt.charAt(1)+value+randomSalt.charAt(2)+randomSalt.charAt(3);
        return md5(val);
    }

    public static String inputPwdToDBPwd(String value,String randomSalt){
        return formPwdToDBPwd(inputPwdToFormPwd(value),randomSalt);
    }

    @Test
    public void md5Test(){
        System.out.printf(inputPwdToDBPwd("123456", "1a2b3c4d"));
    }

}
