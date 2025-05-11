package ssafy.horong.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ssafy.horong.domain.community.dto.NotificationKafkaMessage;
import ssafy.horong.domain.community.entity.Notification;
import ssafy.horong.domain.community.entity.Post;
import ssafy.horong.domain.community.repository.NotificationRepository;
import ssafy.horong.domain.member.entity.User;

import java.time.LocalDateTime;

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
     * @param sender 알림 발신자
     * @param receiver 알림 수신자
     * @param post 관련 게시물
     * @param type 알림 타입
     * @return 생성된 알림
     */
    public Notification createPostNotification(User sender, User receiver, Post post, String type) {
        Notification notification = Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .postId(post.getId())
                .type(type)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        return notificationRepository.save(notification);
    }

    /**
     * 메시지 관련 알림 생성
     * 
     * @param sender 알림 발신자
     * @param receiver 알림 수신자
     * @param messageId 관련 메시지 ID
     * @param type 알림 타입
     * @return 생성된 알림
     */
    public Notification createMessageNotification(User sender, User receiver, Long messageId, String type) {
        Notification notification = Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .messageId(messageId)
                .type(type)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        return notificationRepository.save(notification);
    }

    /**
     * 알림 메시지 생성
     * 
     * @param notification 알림 객체
     * @param language 언어
     * @return 알림 메시지
     */
    public NotificationKafkaMessage createNotificationMessage(Notification notification, String language) {
        return NotificationKafkaMessage.builder()
                .notificationId(notification.getId())
                .receiverId(notification.getReceiver().getId())
                .language(language)
                .build();
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
