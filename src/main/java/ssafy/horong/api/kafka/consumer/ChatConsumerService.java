package ssafy.horong.api.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.horong.api.chat.response.HorongChatMessageResponse;
import ssafy.horong.common.util.NotificationUtil;
import ssafy.horong.domain.chat.dto.ChatKafkaMessage;
import ssafy.horong.domain.chat.entity.Chat;
import ssafy.horong.domain.chat.entity.ChatRoom;
import ssafy.horong.domain.chat.repository.ChatRepository;
import ssafy.horong.domain.chat.repository.ChatRoomRepository;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.repository.UserRepository;

import java.util.Optional;

/**
 * 채팅 관련 카프카 메시지를 소비하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatConsumerService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final NotificationUtil notificationUtil;

    /**
     * 채팅 메시지를 처리하는 카프카 리스너
     * @param message 채팅 메시지
     */
    @KafkaListener(
            topics = "horong-chats",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeChatMessage(@Payload ChatKafkaMessage message) {
        log.info("Received chat message: {}", message.getId());
        
        try {
            switch (message.getMessageType()) {
                case NEW_MESSAGE -> handleNewMessage(message);
                case CHAT_LOG_SAVED -> handleChatLogSaved(message);
                case ROOM_CREATED -> handleRoomCreated(message);
                case USER_JOINED -> handleUserJoined(message);
                case USER_LEFT -> handleUserLeft(message);
                default -> log.warn("Unknown chat message type: {}", message.getMessageType());
            }
        } catch (Exception e) {
            log.error("Error processing chat message: {}", e.getMessage(), e);
        }
    }

    /**
     * 새 채팅 메시지 처리
     */
    @Transactional
    public void handleNewMessage(ChatKafkaMessage message) {
        log.info("Processing new chat message for room: {}", message.getRoomId());

        // 채팅방 찾기
        Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(message.getRoomId());
        if (chatRoomOpt.isEmpty()) {
            log.error("Chat room not found: {}", message.getRoomId());
            return;
        }
        
        // 사용자 찾기
        Optional<User> userOpt = userRepository.findById(message.getUserId());
        if (userOpt.isEmpty()) {
            log.error("User not found: {}", message.getUserId());
            return;
        }
        
        // 채팅방과 사용자
        ChatRoom chatRoom = chatRoomOpt.get();
        
        // 메시지가 있다면 DB에 저장
        if (message.getMessages() != null && !message.getMessages().isEmpty()) {
            for (HorongChatMessageResponse chatMessage : message.getMessages()) {
                Chat chat = Chat.builder()
                        .content(chatMessage.content())
                        .authorType(chatMessage.authorType())
                        .room(chatRoom)
                        .build();
                
                chatRepository.save(chat);
            }
            
            log.info("Saved {} chat messages to database", message.getMessages().size());
        }
        
        // TODO: 실시간 메시지 전송 (웹소켓이나 SSE 등으로)
    }

    /**
     * 채팅 로그 저장 완료 처리
     */
    public void handleChatLogSaved(ChatKafkaMessage message) {
        log.info("Chat log saved for room: {}", message.getRoomId());
        // 추가 처리 로직이 필요하다면 여기에 구현
    }

    /**
     * 채팅방 생성 처리
     */
    @Transactional
    public void handleRoomCreated(ChatKafkaMessage message) {
        log.info("New chat room created: {}", message.getRoomId());
        // 추가 처리 로직이 필요하다면 여기에 구현
    }

    /**
     * 사용자 채팅방 입장 처리
     */
    public void handleUserJoined(ChatKafkaMessage message) {
        log.info("User {} joined chat room: {}", message.getUserId(), message.getRoomId());
        // 추가 처리 로직이 필요하다면 여기에 구현
    }

    /**
     * 사용자 채팅방 퇴장 처리
     */
    public void handleUserLeft(ChatKafkaMessage message) {
        log.info("User {} left chat room: {}", message.getUserId(), message.getRoomId());
        // 추가 처리 로직이 필요하다면 여기에 구현
    }
}
