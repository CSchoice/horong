package ssafy.horong.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ssafy.horong.api.chat.response.HorongChatMessageResponse;
import ssafy.horong.domain.chat.entity.Chat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 카프카를 통해 채팅 데이터를 전달하기 위한 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatKafkaMessage {
    private String id;
    private Long roomId;       // 채팅방 ID
    private Long userId;       // 사용자 ID
    private ChatMessageType messageType;  // 메시지 유형
    private List<HorongChatMessageResponse> messages;  // 메시지 목록
    private LocalDateTime timestamp;
    
    /**
     * 채팅 메시지 이벤트 타입
     */
    public enum ChatMessageType {
        NEW_MESSAGE,           // 새로운 메시지
        CHAT_LOG_SAVED,        // 채팅 로그 저장됨
        ROOM_CREATED,          // 채팅방 생성됨
        USER_JOINED,           // 사용자 입장
        USER_LEFT              // 사용자 퇴장
    }
    
    /**
     * 기본 정보로 카프카 메시지 생성
     */
    public static ChatKafkaMessage of(Long roomId, Long userId, List<HorongChatMessageResponse> messages, ChatMessageType messageType) {
        return ChatKafkaMessage.builder()
                .id(UUID.randomUUID().toString())
                .roomId(roomId)
                .userId(userId)
                .messages(messages)
                .messageType(messageType)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 단일 메시지에 대한 카프카 메시지 생성
     */
    public static ChatKafkaMessage fromChatMessage(Chat chat, Long userId) {
        HorongChatMessageResponse response = new HorongChatMessageResponse(
                chat.getContent(),
                chat.getAuthorType(),
                chat.getCreatedAt()
        );
        
        return ChatKafkaMessage.builder()
                .id(UUID.randomUUID().toString())
                .roomId(chat.getRoom().getId())
                .userId(userId)
                .messages(List.of(response))
                .messageType(ChatMessageType.NEW_MESSAGE)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
