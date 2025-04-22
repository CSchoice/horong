// SaveChatLogCommand.java
package ssafy.horong.domain.chat.command;

import ssafy.horong.api.chat.request.HorongChatContentRequest;

import java.util.List;

public record SaveChatLogCommand(
        List<HorongChatContentRequest> chatContents
) {
}
