package ssafy.horong.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ssafy.horong.domain.community.entity.Notification;
import ssafy.horong.domain.community.entity.Notification.NotificationType;
import ssafy.horong.domain.community.entity.Post;
import ssafy.horong.domain.community.entity.Message;
import ssafy.horong.domain.community.repository.NotificationRepository;
import ssafy.horong.domain.community.dto.NotificationKafkaMessage;
import ssafy.horong.domain.member.common.Language;
import ssafy.horong.domain.member.entity.User;


import java.util.List;
import ssafy.horong.api.community.response.NotificationResponse;

/**
 * 알림 관련 유틸리티 클래스
 * 알림 생성 및 전송을 위한 공통 로직을 제공합니다.
 */
@Component
@RequiredArgsConstructor
public class NotificationUtil {

    private final NotificationRepository notificationRepository;

    /**
     * 게시물 관련 알림 생성
     *
     * @param sender   알림을 보내는 사용자
     * @param receiver 알림을 받는 사용자
     * @param post     관련 게시물
     * @param type     알림 타입
     * @return 생성된 알림
     */
    public Notification createPostNotification(User sender, User receiver, Post post, NotificationType type) {
        Notification notification = Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .post(post) // post 필드 설정
                .type(type)
                .isRead(false)
                .build();
        
        return notificationRepository.save(notification);
    }

    /**
     * 메시지 관련 알림 생성
     *
     * @param sender   알림을 보내는 사용자
     * @param receiver 알림을 받는 사용자
     * @param message  관련 메시지
     * @param type     알림 타입
     * @return 생성된 알림
     */
    public Notification createMessageNotification(User sender, User receiver, Message message, NotificationType type) {
        Notification notification = Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .message(message) // message 필드 설정
                .type(type)
                .isRead(false)
                .build();
        
        return notificationRepository.save(notification);
    }

    /**
     * 알림을 카프카 메시지로 변환
     *
     * @param notification 알림 객체
     * @param language     언어 설정
     * @return 카프카 메시지 객체
     */
    public NotificationKafkaMessage createNotificationMessage(Notification notification, Language language) {
        return NotificationKafkaMessage.fromNotification(notification, language);
    }
    
    /**
     * 알림 목록을 사용자에게 전송
     *
     * @param notifications 알림 응답 목록
     * @param userId 사용자 ID
     * @return 카프카 메시지 객체
     */
    public NotificationKafkaMessage sendNotificationToUser(List<NotificationResponse> notifications, Long userId) {
        return NotificationKafkaMessage.of(
                userId,
                notifications,
                NotificationKafkaMessage.NotificationType.NEW_NOTIFICATION
        );
    }

    /**
     * 알림을 읽음 상태로 변경
     * 
     * @param notificationId 알림 ID
     * @return 업데이트된 알림
     */
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
        
        notification.setRead(true);
        return notificationRepository.save(notification);
    }
}
