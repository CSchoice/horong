package ssafy.horong.common.exception.board;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.CommunityErrorCode;

public class CommentNotFoundException extends BaseException {
    public CommentNotFoundException() {
        super(CommunityErrorCode.COMMENT_NOT_FOUND);
    }
}
