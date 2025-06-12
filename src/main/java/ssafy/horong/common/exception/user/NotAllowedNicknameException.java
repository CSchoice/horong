package ssafy.horong.common.exception.user;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class NotAllowedNicknameException extends BaseException {
    public NotAllowedNicknameException() {
        super(UserErrorCode.NOT_ALLOWED_NICKNAME);
    }
}
