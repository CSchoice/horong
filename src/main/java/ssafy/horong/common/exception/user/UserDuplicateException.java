package ssafy.horong.common.exception.user;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class UserDuplicateException extends BaseException {
    public UserDuplicateException() {
        super(UserErrorCode.USER_DUPLICATE);
    }
}
