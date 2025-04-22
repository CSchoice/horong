package ssafy.horong.api.horongChat.response;

import java.util.List;

public record HorongChatRoomListResponse(
        List<HorongChatRoomResponse> chatList
) {
}
