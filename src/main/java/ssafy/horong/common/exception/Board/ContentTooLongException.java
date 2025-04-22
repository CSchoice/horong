package ssafy.horong.common.exception.Board;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.CommunityErrorCode;

public class ContentTooLongException extends BaseException {
    public ContentTooLongException() {
        super(CommunityErrorCode.CONTENT_TOO_LONG);
    }
}
