package ssafy.horong.api.community;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.common.util.NotificationUtil;
import ssafy.horong.common.util.UserUtil;
import ssafy.horong.domain.community.entity.Notification;
import ssafy.horong.domain.community.service.NotificationService;
import ssafy.horong.domain.member.common.Language;
import ssafy.horong.domain.member.repository.UserRepository;


@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationUtil notificationUtil;
    private final UserRepository userRepository;
    private final UserUtil userUtil;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "알림 읽음 처리", description = "알림을 읽음 처리합니다.")
    @PostMapping("/{notificationId}")
    public CommonResponse<Void> markAsRead(@PathVariable Long notificationId, @RequestParam Notification.NotificationType type) {
        notificationService.markAsRead(notificationId, type);
        return CommonResponse.ok("알림이 읽음 처리되었습니다.", null);
    }

    @Operation(summary = "알림 스트림", description = "알림을 스트림으로 전송합니다.")
    @GetMapping("/stream")
    public SseEmitter streamNotifications() {
        System.out.println("streamNotifications 메서드 호출됨 - 호출 원인 확인 필요");
        return notificationUtil.createSseEmitter();
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "상대의 언어를 조회", description = "상대의 언어를 조회합니다.")
    @GetMapping("/language/{userId}")
    public CommonResponse<Language> getLanguage(@PathVariable Long userId) {
        return CommonResponse.ok(userRepository.findByUserLongId(userId).orElseThrow(null).getLanguage());
    }
}