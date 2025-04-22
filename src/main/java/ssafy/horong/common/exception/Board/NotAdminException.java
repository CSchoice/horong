package ssafy.horong.common.exception.Board;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.CommunityErrorCode;

public class NotAdminException extends BaseException {
    public NotAdminException() {
        super(CommunityErrorCode.NOT_ADMIN);
    }
}
