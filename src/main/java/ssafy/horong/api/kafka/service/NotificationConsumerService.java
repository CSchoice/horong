package ssafy.horong.api.kafka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ssafy.horong.common.util.NotificationSseUtil;
import ssafy.horong.domain.community.dto.NotificationKafkaMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumerService {

    private final NotificationSseUtil notificationSseUtil;

    /**
     * horong-notifications 토픽의 메시지를 수신하여 SSE로 푸시
     */
    @KafkaListener(
            topics            = "horong-notifications",
            containerFactory  = "notificationKafkaListenerContainerFactory"
    )
    public void consume(NotificationKafkaMessage message) {
        log.info("Notification event consumed for userId={}", message.getUserId());
        // NotificationKafkaMessage 안의 notifications 리스트를 그대로 SSE 전송
        notificationSseUtil.sendNotificationToUser(
                message.getNotifications(),
                message.getUserId()
        );
    }
}
