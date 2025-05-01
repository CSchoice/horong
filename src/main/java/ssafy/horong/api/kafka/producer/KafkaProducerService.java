package ssafy.horong.api.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ssafy.horong.api.kafka.dto.KafkaEventMessage;
import ssafy.horong.domain.chat.dto.ChatKafkaMessage;
import ssafy.horong.domain.community.dto.NotificationKafkaMessage;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String NOTIFICATION_TOPIC = "horong-notifications";
    private static final String EVENTS_TOPIC = "horong-events";
    private static final String CHAT_TOPIC = "horong-chats";

    /**
     * 알림 메시지를 카프카에 발행 (NotificationKafkaMessage 형식)
     * @param message 알림 카프카 메시지
     */
    public void sendNotification(NotificationKafkaMessage message) {
        log.info("Sending notification kafka message: {}", message.getId());

        // 메시지 키로 사용자 ID를 사용하여 같은 사용자의 메시지는 같은 파티션에 저장되도록 함
        kafkaTemplate.send(NOTIFICATION_TOPIC, message.getUserId().toString(), message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Message sent successfully: topic={}, partition={}, offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send message: {}", ex.getMessage(), ex);
                    }
                });
    }

    /**
     * 채팅 메시지를 카프카에 발행
     * @param message 채팅 메시지
     */
    public void sendChatMessage(ChatKafkaMessage message) {
        log.info("Sending chat message: {}", message.getId());

        // 메시지 키로 채팅방 ID를 사용하여 같은 채팅방의 메시지는 같은 파티션에 저장되도록 함
        kafkaTemplate.send(CHAT_TOPIC, message.getRoomId().toString(), message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Chat message sent successfully: topic={}, partition={}, offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send chat message: {}", ex.getMessage(), ex);
                    }
                });
    }

    /**
     * 일반적인 이벤트 메시지를 카프카에 발행
     * @param eventType 이벤트 타입
     * @param payload 이벤트 페이로드
     * @param source 이벤트 소스
     */
    public void sendEvent(String eventType, String payload, String source) {
        KafkaEventMessage event = KafkaEventMessage.builder()
                .id(UUID.randomUUID().toString())
                .eventType(eventType)
                .payload(payload)
                .timestamp(LocalDateTime.now())
                .source(source)
                .build();

        log.info("Sending event message: {}", event);

        kafkaTemplate.send(EVENTS_TOPIC, event.getId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Event sent successfully: topic={}, partition={}, offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send event: {}", ex.getMessage(), ex);
                    }
                });
    }
}
