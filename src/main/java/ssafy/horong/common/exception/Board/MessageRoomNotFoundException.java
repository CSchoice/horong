package ssafy.horong.common.exception.Board;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.CommunityErrorCode;

public class MessageRoomNotFoundException extends BaseException {
    public MessageRoomNotFoundException() {
        super(CommunityErrorCode.CHATROOM_NOT_FOUND);
    }
}
