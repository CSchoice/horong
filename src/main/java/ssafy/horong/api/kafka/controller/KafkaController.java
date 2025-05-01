package ssafy.horong.api.kafka.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssafy.horong.api.kafka.dto.NotificationMessage;
import ssafy.horong.api.kafka.producer.KafkaProducerService;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
@Tag(name = "Kafka", description = "카프카 메시지 발행 API")
public class KafkaController {

    private final KafkaProducerService kafkaProducerService;

    @PostMapping("/notifications")
    @Operation(summary = "알림 메시지 발행", description = "카프카를 통해 알림 메시지를 발행합니다")
    public ResponseEntity<NotificationMessage> sendNotification(@RequestBody NotificationMessage request) {
        log.info("Notification message publish request: {}", request);
        
        // 기본값 설정
        if (request.getId() == null) {
            request.setId(UUID.randomUUID().toString());
        }
        
        if (request.getCreatedAt() == null) {
            request.setCreatedAt(LocalDateTime.now());
        }
        
        // 메시지 발행
        kafkaProducerService.sendNotification(request);
        
        return ResponseEntity.ok(request);
    }

    @PostMapping("/events")
    @Operation(summary = "이벤트 메시지 발행", description = "카프카를 통해 이벤트 메시지를 발행합니다")
    public ResponseEntity<String> sendEvent(@RequestBody EventRequest request) {
        log.info("Event message publish request: {}", request);
        
        // 메시지 발행
        kafkaProducerService.sendEvent(
                request.getEventType(),
                request.getPayload(),
                request.getSource()
        );
        
        return ResponseEntity.ok("Event published successfully");
    }
    
    // 이벤트 요청 DTO
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class EventRequest {
        private String eventType;
        private String payload;
        private String source;
    }
}
