package ssafy.horong.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ssafy.horong.api.community.response.NotificationResponse;
import ssafy.horong.domain.community.entity.Notification;
import ssafy.horong.domain.member.common.Language;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 카프카를 통해 알림 데이터를 전달하기 위한 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationKafkaMessage {
    private String id;
    private Long userId;  // 알림을 받을 사용자 ID
    private List<NotificationResponse> notifications; // 알림 목록
    private NotificationType type; // 알림 유형
    private LocalDateTime timestamp;
    
    /**
     * 알림 이벤트 타입
     */
    public enum NotificationType {
        NEW_NOTIFICATION,      // 새로운 알림 생성
        READ_NOTIFICATION,     // 알림 읽음 처리
        MERGED_NOTIFICATIONS   // 병합된 알림 데이터
    }
    
    /**
     * 기본 정보로 카프카 메시지 생성
     */
    public static NotificationKafkaMessage of(Long userId, List<NotificationResponse> notifications, NotificationType type) {
        return NotificationKafkaMessage.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .notifications(notifications)
                .type(type)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * 단일 알림에 대한 카프카 메시지 생성
     */
    public static NotificationKafkaMessage fromNotification(Notification notification, Language language) {
        NotificationResponse response = NotificationResponse.from(notification, language);
        return NotificationKafkaMessage.builder()
                .id(UUID.randomUUID().toString())
                .userId(notification.getReceiver().getId())
                .notifications(List.of(response))
                .type(NotificationType.NEW_NOTIFICATION)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
