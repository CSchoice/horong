package ssafy.horong.domain.horongChat.service;

import ssafy.horong.api.horongChat.response.HorongChatRoomListResponse;
import ssafy.horong.api.horongChat.response.HorongChatRoomResponse;
import ssafy.horong.domain.horongChat.command.SaveChatLogCommand;

public interface HorongChatService {
    void saveChatLog(SaveChatLogCommand command);
    HorongChatRoomListResponse getChatRoomList();
    HorongChatRoomResponse getChatRoom(Long chatId);
}
