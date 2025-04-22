package ssafy.horong.api.chat.response;

import java.util.List;

public record HorongChatRoomListResponse(
        List<HorongChatRoomResponse> chatList
) {
}
