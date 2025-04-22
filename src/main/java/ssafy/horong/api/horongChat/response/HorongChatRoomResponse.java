package ssafy.horong.api.horongChat.response;

import java.util.List;

public record HorongChatRoomResponse(
        Long chatRoomId,
        List<HorongChatMessageResponse> chatContentList
) {
}
