package ssafy.horong.common.exception.user;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class LanguageNotValidException extends BaseException {
    public LanguageNotValidException() {
        super(UserErrorCode.LANGUAGE_NOT_VALID);
    }
}
