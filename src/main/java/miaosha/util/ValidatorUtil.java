package miaosha.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {
    private static final Pattern pattern = Pattern.compile("1\\d{10}");
    public static boolean isMobile(String value) {
        if(StringUtils.isEmpty(value)){
            return false;
        }
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
