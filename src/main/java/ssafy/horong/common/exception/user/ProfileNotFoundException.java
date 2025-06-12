package ssafy.horong.common.exception.user;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.UserErrorCode;

public class ProfileNotFoundException extends BaseException {
    public ProfileNotFoundException() {
        super(UserErrorCode.PROFILE_NOT_FOUND);
    }
}