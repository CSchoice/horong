package ssafy.horong.api.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ssafy.horong.api.kafka.dto.KafkaEventMessage;
import ssafy.horong.api.kafka.dto.NotificationMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    /**
     * 알림 메시지를 수신하는 리스너
     * @param message 알림 메시지
     */
    @KafkaListener(
            topics = "horong-notifications",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeNotification(@Payload NotificationMessage message) {
        log.info("Received notification message: {}", message);
        
        try {
            // 여기서 알림 메시지 처리 로직 구현
            // 예: 데이터베이스에 저장, 웹소켓으로 클라이언트에 전송 등
            processNotification(message);
        } catch (Exception e) {
            log.error("Error processing notification message: {}", e.getMessage(), e);
        }
    }

    /**
     * 이벤트 메시지를 수신하는 리스너
     * @param message 이벤트 메시지
     */
    @KafkaListener(
            topics = "horong-events",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeEvent(@Payload KafkaEventMessage message) {
        log.info("Received event message: {}", message);
        
        try {
            // 여기서 이벤트 메시지 처리 로직 구현
            // 이벤트 타입에 따라 다른 처리를 할 수 있음
            processEvent(message);
        } catch (Exception e) {
            log.error("Error processing event message: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 알림 메시지 처리 로직
     * @param message 알림 메시지
     */
    private void processNotification(NotificationMessage message) {
        // 실제 구현에서는 알림 메시지를 처리하는 로직 추가
        // 예: 데이터베이스에 저장, 웹소켓을 통해 실시간 알림 전송 등
        log.info("Processing notification: {}", message.getTitle());
    }
    
    /**
     * 이벤트 메시지 처리 로직
     * @param message 이벤트 메시지
     */
    private void processEvent(KafkaEventMessage message) {
        // 이벤트 타입에 따라 다른 처리 로직 수행
        switch (message.getEventType()) {
            case KafkaEventMessage.EVENT_TYPE_NOTIFICATION:
                log.info("Processing notification event");
                break;
            case KafkaEventMessage.EVENT_TYPE_USER_ACTION:
                log.info("Processing user action event");
                break;
            case KafkaEventMessage.EVENT_TYPE_SYSTEM:
                log.info("Processing system event");
                break;
            default:
                log.info("Processing unknown event type: {}", message.getEventType());
        }
    }
}
