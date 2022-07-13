package miaosha.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CodeMsg {
    private int code;
    private String msg;

    //通用的错误码
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常：%s");
    public static CodeMsg REQUEST_ILLEGAL = new CodeMsg(500101, "非法请求");
    //登录模块 5002XX
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session不存在或者已经失效");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "登录密码不能为空");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "手机号不能为空");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500213, "手机号格式错误");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "手机号不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "密码错误");

    //商品模块 5003XX

    //订单模块 5004XX
    public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400, "订单不存在");
    public static CodeMsg ORDER_REFUSED_CHECK = new CodeMsg(500400, "无法查询该订单");

    //秒杀模块 5005XX
    public static CodeMsg MIAOSHA_SUCCESS = new CodeMsg(500500,"%s");
    public static CodeMsg MIAOSHA_WAITING = new CodeMsg(500501,"排队中");
    public static CodeMsg STOCK_EMPTY = new CodeMsg(500502,"商品已售罄");
    public static CodeMsg MIAOSHA_REPEAT = new CodeMsg(500503,"用户只能秒杀商品一次");
    public static CodeMsg MIAOSHA_REFUSE = new CodeMsg(500504,"当前访问人数太多，请稍后再试");

    public CodeMsg fillArgs(String...args){
        int code = this.code;
        String msg = String.format(this.msg, args);
        return new CodeMsg(code,msg);
    }

    @Override
    public String toString() {
        return "CodeMsg{" +
                "code=" + code +
                ", Msg='" + msg + '\'' +
                '}';
    }
}
