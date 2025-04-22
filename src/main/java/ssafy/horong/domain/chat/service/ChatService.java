package ssafy.horong.domain.chat.service;

import ssafy.horong.api.chat.response.HorongChatRoomListResponse;
import ssafy.horong.api.chat.response.HorongChatRoomResponse;
import ssafy.horong.domain.chat.command.SaveChatLogCommand;

public interface ChatService {
    void saveChatLog(SaveChatLogCommand command);
    HorongChatRoomListResponse getChatRoomList();
    HorongChatRoomResponse getChatRoom(Long chatId);
}
