package ssafy.horong.common.exception;

import lombok.Getter;
import ssafy.horong.common.exception.errorcode.BaseErrorCode;

@Getter
public class BaseException extends RuntimeException {
    private final BaseErrorCode errorCode;
    private final String message;
    
    public BaseException(BaseErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }
    
    public BaseException(BaseErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
    
    @Override
    public String getMessage() {
        return this.message;
    }
}
