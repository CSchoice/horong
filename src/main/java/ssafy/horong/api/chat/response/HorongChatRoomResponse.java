package ssafy.horong.api.chat.response;

import java.util.List;

public record HorongChatRoomResponse(
        Long chatRoomId,
        List<HorongChatMessageResponse> chatContentList
) {
}
