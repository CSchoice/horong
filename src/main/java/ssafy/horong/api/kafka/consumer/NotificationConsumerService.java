package ssafy.horong.api.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ssafy.horong.common.util.NotificationUtil;
import ssafy.horong.domain.community.dto.NotificationKafkaMessage;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.repository.UserRepository;

/**
 * 알림 관련 카프카 메시지를 소비하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumerService {

    private final NotificationUtil notificationUtil;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * 알림 메시지를 처리하는 카프카 리스너
     * @param message 알림 메시지
     */
    @KafkaListener(
            topics = "horong-notifications",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeNotification(@Payload NotificationKafkaMessage message) {
        log.info("Received notification message: {}", message.getId());
        
        try {
            switch (message.getType()) {
                case NEW_NOTIFICATION -> handleNewNotification(message);
                case READ_NOTIFICATION -> handleReadNotification(message);
                case MERGED_NOTIFICATIONS -> handleMergedNotifications(message);
                default -> log.warn("Unknown notification type: {}", message.getType());
            }
        } catch (Exception e) {
            log.error("Error processing notification message: {}", e.getMessage(), e);
        }
    }

    /**
     * 새 알림 처리
     */
    private void handleNewNotification(NotificationKafkaMessage message) {
        log.info("Processing new notification for user: {}", message.getUserId());
        
        // 메시지에서 사용자에게 알림 전송
        notificationUtil.sendNotificationToUser(message.getNotifications(), message.getUserId());
        
        // TODO: 추가 처리 (예: 푸시 알림, 이메일 등)
    }

    /**
     * 알림 읽음 처리
     */
    private void handleReadNotification(NotificationKafkaMessage message) {
        log.info("Processing read notification for user: {}", message.getUserId());
        
        // 메시지에서 사용자에게 업데이트된 알림 전송
        notificationUtil.sendNotificationToUser(message.getNotifications(), message.getUserId());
    }

    /**
     * 병합된 알림 처리
     */
    private void handleMergedNotifications(NotificationKafkaMessage message) {
        log.info("Processing merged notifications for user: {}", message.getUserId());
        
        // 메시지에서 사용자에게 병합된 알림 전송
        notificationUtil.sendNotificationToUser(message.getNotifications(), message.getUserId());
    }
}
