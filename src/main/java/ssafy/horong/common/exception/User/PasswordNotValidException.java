package ssafy.horong.common.exception.User;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class PasswordNotValidException extends BaseException {
    public PasswordNotValidException() {
        super(UserErrorCode.PASSWORD_NOT_VALID);
    }
}
