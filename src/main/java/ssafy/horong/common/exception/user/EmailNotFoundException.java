package ssafy.horong.common.exception.user;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class EmailNotFoundException extends BaseException {
    public EmailNotFoundException() {
        super(UserErrorCode.EMAIL_NOT_FOUND);
    }
}
