package ssafy.horong.common.exception.board;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.CommunityErrorCode;

public class NotAuthenticatedException extends BaseException {
    public NotAuthenticatedException() {
        super(CommunityErrorCode.NOT_AUTHENTICATED);
    }
}
