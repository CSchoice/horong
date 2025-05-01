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
public class KafkaEventMessage {
    private String id;
    private String eventType;
    private String payload;
    private LocalDateTime timestamp;
    private String source;
    
    // 기본 이벤트 타입 정의
    public static final String EVENT_TYPE_NOTIFICATION = "NOTIFICATION";
    public static final String EVENT_TYPE_USER_ACTION = "USER_ACTION";
    public static final String EVENT_TYPE_SYSTEM = "SYSTEM";
}
