package ssafy.horong.common.exception.horongChat;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.HorongChatErrorCode;

public class ChatRoomAccessDeniedException extends BaseException {
    public ChatRoomAccessDeniedException() {
        super(HorongChatErrorCode.CHATROOM_NOT_AUTHENTICATED);
    }

}
