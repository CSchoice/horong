package ssafy.horong.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.horong.api.chat.request.HorongChatContentRequest;
import ssafy.horong.api.chat.response.HorongChatMessageResponse;
import ssafy.horong.api.chat.response.HorongChatRoomListResponse;
import ssafy.horong.api.chat.response.HorongChatRoomResponse;
import ssafy.horong.api.kafka.producer.KafkaProducerService;
import ssafy.horong.common.exception.User.MemberNotFoundException;
import ssafy.horong.common.exception.horongChat.ChatRoomAccessDeniedException;
import ssafy.horong.common.exception.security.NotAuthenticatedException;
import ssafy.horong.common.util.SecurityUtil;
import ssafy.horong.domain.chat.repository.ChatRepository;
import ssafy.horong.domain.chat.repository.ChatRoomRepository;
import ssafy.horong.domain.chat.command.SaveChatLogCommand;
import ssafy.horong.domain.chat.dto.ChatKafkaMessage;
import ssafy.horong.domain.chat.entity.Chat;
import ssafy.horong.domain.chat.entity.ChatRoom;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {
    private final ChatRepository horongChatRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public void saveChatLog(SaveChatLogCommand command) {

        // 현재 로그인한 사용자 찾기
        User currentUser = getCurrentLoggedInMember();

        // 1. 새로운 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .user(currentUser)  // 현재 사용자를 채팅방에 연결
                .build();

        chatRoomRepository.save(chatRoom);  // 채팅방 저장
        
        // 카프카로 채팅방 생성 이벤트 발행
        ChatKafkaMessage roomCreatedEvent = ChatKafkaMessage.builder()
                .id(java.util.UUID.randomUUID().toString())
                .roomId(chatRoom.getId())
                .userId(currentUser.getId())
                .messageType(ChatKafkaMessage.ChatMessageType.ROOM_CREATED)
                .timestamp(java.time.LocalDateTime.now())
                .build();
                
        kafkaProducerService.sendChatMessage(roomCreatedEvent);

        // 채팅 메시지 목록 저장을 위한 리스트
        List<HorongChatMessageResponse> chatMessages = new ArrayList<>();

        // 2. 명령 객체에서 채팅 내용을 가져와서 해당 방에 메시지 저장 (양방향 연관관계 설정 없이 직접 room 설정)
        for (HorongChatContentRequest content : command.chatContents()) {
            Chat chatEntity = Chat.builder()
                    .content(content.content())  // 메시지 내용 설정
                    .authorType(content.authorType())  // Enum 타입으로 authorType 설정
                    .room(chatRoom)  // 직접적으로 방과 연결 (양방향 연관관계 메서드 없이 처리)
                    .build();

            horongChatRepository.save(chatEntity);  // DB에 메시지 저장
            
            // 응답 목록에 메시지 추가
            chatMessages.add(new HorongChatMessageResponse(
                    chatEntity.getContent(),
                    chatEntity.getAuthorType(),
                    chatEntity.getCreatedAt()
            ));
        }
        
        // 카프카로 채팅 로그 저장 이벤트 발행
        ChatKafkaMessage chatLogEvent = ChatKafkaMessage.builder()
                .id(java.util.UUID.randomUUID().toString())
                .roomId(chatRoom.getId())
                .userId(currentUser.getId())
                .messages(chatMessages)
                .messageType(ChatKafkaMessage.ChatMessageType.CHAT_LOG_SAVED)
                .timestamp(java.time.LocalDateTime.now())
                .build();
                
        kafkaProducerService.sendChatMessage(chatLogEvent);
    }

    public HorongChatRoomListResponse getChatRoomList() {
        // 현재 로그인한 사용자를 찾음
        User currentUser = getCurrentLoggedInMember();

        // 1. 사용자가 속한 모든 채팅방 조회
        List<ChatRoom> chatRooms = chatRoomRepository.findByUser(currentUser);

        // 2. 각 채팅방에 속한 메시지들을 가져와서 ChatRoomResponse로 변환
        List<HorongChatRoomResponse> horongChatRoomRespons = chatRooms.stream()
                .map(room -> {
                    List<HorongChatMessageResponse> chatContents = room.getChatMessages().stream()
                            .map(chat -> new HorongChatMessageResponse(
                                    chat.getContent(),
                                    chat.getAuthorType(),
                                    chat.getCreatedAt()
                            ))
                            .toList();

                    return new HorongChatRoomResponse(room.getId(), chatContents);
                })
                .toList();

        return new HorongChatRoomListResponse(horongChatRoomRespons);
    }

    public HorongChatRoomResponse getChatRoom(Long roomId) {

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow();

        if (chatRoom.getUser() != getCurrentLoggedInMember()) {
            throw new ChatRoomAccessDeniedException();
        }

        // 2. 해당 방에 속한 모든 메시지를 ChatContentResponse로 변환
        List<HorongChatMessageResponse> chatContentList = chatRoom.getChatMessages().stream()
                .map(chat -> new HorongChatMessageResponse(
                        chat.getContent(),
                        chat.getAuthorType(),
                        chat.getCreatedAt()
                ))
                .toList();

        return new HorongChatRoomResponse(roomId, chatContentList);  // 응답 반환
    }

    private User getCurrentLoggedInMember() {
        Long userId = SecurityUtil.getLoginMemberId()
                .orElseThrow(NotAuthenticatedException::new);
        User user = userRepository.findById(userId)
                .orElseThrow(MemberNotFoundException::new);

        if (user.isDeleted()) {
            throw new NotAuthenticatedException();
        }
        return user;
    }
}
