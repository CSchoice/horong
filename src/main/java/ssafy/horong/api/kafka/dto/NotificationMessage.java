package ssafy.horong.api.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private String id;
    private String recipientId;  // 알림을 받을 사용자 ID
    private String title;        // 알림 제목
    private String content;      // 알림 내용
    private String type;         // 알림 유형 (SYSTEM, USER, etc.)
    private String actionUrl;    // 알림 클릭 시 이동할 URL
    private boolean read;        // 읽음 여부
    private LocalDateTime createdAt; // 생성 시간
    
    // 기본 알림 타입 정의
    public static final String TYPE_SYSTEM = "SYSTEM";
    public static final String TYPE_USER = "USER";
    public static final String TYPE_MARKETING = "MARKETING";
}
