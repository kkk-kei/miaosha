package miaosha.result;

public class CodeMsg {
    private int code;
    private String Msg;

    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session不存在或者已经失效");


    private CodeMsg() {
    }

    private CodeMsg(int code, String msg) {
        this.code = code;
        Msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    @Override
    public String toString() {
        return "CodeMsg{" +
                "code=" + code +
                ", Msg='" + Msg + '\'' +
                '}';
    }
}
