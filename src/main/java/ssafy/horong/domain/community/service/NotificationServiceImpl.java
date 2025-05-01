package ssafy.horong.domain.community.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.horong.api.community.response.NotificationResponse;
import ssafy.horong.api.kafka.producer.KafkaProducerService;
import ssafy.horong.common.util.NotificationUtil;
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
    private final NotificationUtil notificationUtil;
    private final KafkaProducerService kafkaProducerService; // 카프카 프로듀서 서비스 추가

    @Transactional
    public void markAsRead(Long notificationId, Notification.NotificationType type) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> {
                    final String NOTIFICATION_NOT_FOUND = "알림이 존재하지 않습니다.";
                    return new ResourceNotFoundException(NOTIFICATION_NOT_FOUND);
                });
        notification.markAsRead();
        notificationRepository.save(notification);

        User user = getCurrentUser();
        
        // 기존 방식으로 SSE를 통한 알림 전송 (기존 호환성 유지)
        notificationUtil.sendMergedNotifications(user);
        
        // 카프카를 통한 알림 이벤트 발행 (읽음 처리됨)
        publishNotificationEvent(user);
    }
    
    /**
     * 사용자의 알림을 조회하여 카프카 이벤트로 발행
     * @param user 사용자 정보
     */
    private void publishNotificationEvent(User user) {
        // 읽지 않은 댓글 알림 조회
        List<Notification> unreadCommentNotifications = notificationRepository
                .findByReceiverAndIsReadFalseAndType(user, Notification.NotificationType.COMMENT);

        // 읽지 않은 메시지 알림 조회
        List<Notification> unreadMessageNotifications = notificationRepository
                .findByReceiverAndIsReadFalseAndType(user, Notification.NotificationType.MESSAGE);

        // 모든 알림 합치기
        List<Notification> combinedNotifications = new ArrayList<>();
        combinedNotifications.addAll(unreadCommentNotifications);
        combinedNotifications.addAll(unreadMessageNotifications);

        // 생성 시간 기준 정렬
        combinedNotifications.sort(Comparator.comparing(Notification::getCreatedAt));

        // DTO로 변환
        List<NotificationResponse> notificationResponses = NotificationResponse.convertToNotificationDTOs(combinedNotifications, user.getLanguage());

        // 카프카 이벤트 발행
        NotificationKafkaMessage kafkaMessage = NotificationKafkaMessage.of(
                user.getId(),
                notificationResponses,
                NotificationKafkaMessage.NotificationType.MERGED_NOTIFICATIONS
        );
        
        kafkaProducerService.sendNotification(kafkaMessage);
    }

    private User getCurrentUser() {
        Long userId = SecurityUtil.getLoginMemberId()
                .orElseThrow(() -> {
                    final String LOGIN_USER_NOT_FOUND = "로그인한 사용자가 존재하지 않습니다.";
                    return new RuntimeException(LOGIN_USER_NOT_FOUND);
                });
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    final String USER_NOT_FOUND = "사용자가 존재하지 않습니다.";
                    return new RuntimeException(USER_NOT_FOUND);
                });
    }
}
