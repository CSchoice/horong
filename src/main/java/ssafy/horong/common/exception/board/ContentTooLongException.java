package ssafy.horong.common.exception.board;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.CommunityErrorCode;

public class ContentTooLongException extends BaseException {
    public ContentTooLongException() {
        super(CommunityErrorCode.CONTENT_TOO_LONG);
    }
}
