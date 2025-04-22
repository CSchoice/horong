package ssafy.horong.api.chat.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.horong.domain.chat.command.SaveChatLogCommand;

import java.util.List;

public record SaveChatHistoryRequest(
        @Schema(
                description = "채팅 리스트입니다",
                example = "[{\"content\": \"채팅 내용입니다.\", \"authorType\": \"USER\"}, {\"content\": \"다른 채팅 내용입니다.\", \"authorType\": \"BOT\"}]"
        )
        List<HorongChatContentRequest> chatContents
) {
    public SaveChatLogCommand toCommand() {
        return new SaveChatLogCommand(chatContents);
    }
}
