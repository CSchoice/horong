package ssafy.horong.api.chat.response;

import ssafy.horong.domain.chat.entity.HorongChat;

import java.time.LocalDateTime;

public record HorongChatMessageResponse(
        String content,
        HorongChat.AuthorType authorType,
        LocalDateTime createdAt
) {
}
