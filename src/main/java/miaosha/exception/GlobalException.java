package miaosha.exception;

import lombok.Getter;
import miaosha.result.CodeMsg;

@Getter
public class GlobalException extends RuntimeException{

    private static final long serialVersionUID = 1L;
    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg) {
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }
}
