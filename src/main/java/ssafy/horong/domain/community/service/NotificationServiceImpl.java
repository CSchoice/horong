package ssafy.horong.domain.community.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.horong.api.kafka.producer.KafkaProducerService;
import ssafy.horong.common.util.SecurityUtil;
import ssafy.horong.domain.community.dto.NotificationKafkaMessage;
import ssafy.horong.domain.community.entity.Notification;
import ssafy.horong.domain.community.repository.NotificationRepository;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.repository.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;

    /**
     * 알림을 읽음 처리하고, 변경된 알림 목록을 Kafka로 발행합니다.
     */
    @Transactional
    public void markAsRead(Long notificationId, Notification.NotificationType type) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("알림이 존재하지 않습니다.");
                });

        // 읽음 처리
        notification.markAsRead();
        notificationRepository.save(notification);

        // 현재 사용자 조회
        User user = getCurrentUser();

        // Kafka로 통합 알림 이벤트 발행
        publishNotificationEvent(user);
    }

    /**
     * 읽지 않은 COMMENT, MESSAGE 알림을 조회해
     * NotificationKafkaMessage 형태로 Kafka에 발행합니다.
     */
    private void publishNotificationEvent(User user) {
        // 읽지 않은 댓글 알림 조회
        List<Notification> unreadCommentNotifications = notificationRepository
                .findByReceiverAndIsReadFalseAndType(user, Notification.NotificationType.COMMENT);

        // 읽지 않은 메시지 알림 조회
        List<Notification> unreadMessageNotifications = notificationRepository
                .findByReceiverAndIsReadFalseAndType(user, Notification.NotificationType.MESSAGE);

        // 모든 알림 합치기 및 정렬
        List<Notification> combinedNotifications = new ArrayList<>();
        combinedNotifications.addAll(unreadCommentNotifications);
        combinedNotifications.addAll(unreadMessageNotifications);
        combinedNotifications.sort(Comparator.comparing(Notification::getCreatedAt));

        // DTO 변환 후 Kafka 발행
        List<ssafy.horong.api.community.response.NotificationResponse> notificationResponses =
                ssafy.horong.api.community.response.NotificationResponse
                        .convertToNotificationDTOs(combinedNotifications, user.getLanguage());

        NotificationKafkaMessage kafkaMessage = NotificationKafkaMessage.of(
                user.getId(),
                notificationResponses,
                NotificationKafkaMessage.NotificationType.MERGED_NOTIFICATIONS
        );

        kafkaProducerService.sendNotification(kafkaMessage);
    }

    /**
     * SecurityUtil을 통해 현재 로그인한 사용자 정보를 조회합니다.
     */
    private User getCurrentUser() {
        Long userId = SecurityUtil.getLoginMemberId()
                .orElseThrow(() -> new RuntimeException("로그인한 사용자가 존재하지 않습니다."));
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));
    }
}
