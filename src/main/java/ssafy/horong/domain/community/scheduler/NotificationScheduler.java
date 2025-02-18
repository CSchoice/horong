package ssafy.horong.domain.community.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ssafy.horong.common.util.NotificationUtil;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.repository.UserRepository;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationUtil notificationUtil;
    private final UserRepository userRepository;

    @Transactional
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void notifyUnreadAlerts() {
        List<User> users = userRepository.findAll(); // 모든 사용자 조회

        for (User user : users) {
            notificationUtil.sendMergedNotifications(user); // 각 사용자에 대해 알림 전송
//            log.info("알림 전송 완료: {}", user);
        }
//        log.info("모든 사용자에 대한 알림 전송 완료, {}", users);
    }
}

