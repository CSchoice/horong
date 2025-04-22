package ssafy.horong.api.chat.response;

import ssafy.horong.domain.chat.entity.Chat;

import java.time.LocalDateTime;

public record HorongChatMessageResponse(
        String content,
        Chat.AuthorType authorType,
        LocalDateTime createdAt
) {
}
