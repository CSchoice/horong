package ssafy.horong.api.chat.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.horong.domain.chat.entity.Chat;

public record HorongChatContentRequest(
        @Schema(description = "채팅 내용", example = "채팅 내용입니다.")
        String content,

        @Schema(description = "채팅자 타입", example = "USER")
        Chat.AuthorType authorType
) {
}