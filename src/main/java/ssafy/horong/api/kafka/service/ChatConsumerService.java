package ssafy.horong.api.kafka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ssafy.horong.common.util.ChatSseUtil;
import ssafy.horong.domain.chat.dto.ChatKafkaMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatConsumerService {

    private final ChatSseUtil chatSseUtil;

    @KafkaListener(
            topics           = "horong-chats",
            containerFactory = "chatKafkaListenerContainerFactory"
    )
    public void consume(ChatKafkaMessage message) {
        log.info("Consumed chat event: roomId={}, userId={}, type={}",
                message.getRoomId(), message.getUserId(), message.getMessageType());

        // CHAT_LOG_SAVED 이벤트일 때만 메시지 리스트를 푸시
        if (message.getMessageType() == ChatKafkaMessage.ChatMessageType.CHAT_LOG_SAVED) {
            chatSseUtil.sendChatToUser(
                    message.getMessages(),      // List<HorongChatMessageResponse>
                    message.getUserId()
            );
        }
    }
}
