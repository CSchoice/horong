package ssafy.horong.common.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ssafy.horong.domain.community.entity.Notification;
import ssafy.horong.domain.community.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 일정 주기로 실행되는 배치 작업을 처리하는 스케줄러
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class CleanupScheduler {

    private final NotificationRepository notificationRepository;

    /**
     * 오래된 알림을 정리하는 배치 작업
     * 매일 새벽 2시에 실행
     */
    @Transactional
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupOldNotifications() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        int batchSize = 1000;
        int deletedCount = 0;
        
        while (true) {
            List<Notification> notifications = notificationRepository
                .findOldNotifications(oneMonthAgo, PageRequest.of(0, batchSize));
            
            if (notifications.isEmpty()) {
                break;
            }
            
            notificationRepository.deleteAll(notifications);
            deletedCount += notifications.size();
            
            log.info("삭제된 오래된 알림: {}", deletedCount);
        }
    }
}
