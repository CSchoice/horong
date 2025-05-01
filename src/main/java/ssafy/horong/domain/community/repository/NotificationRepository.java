package ssafy.horong.domain.community.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssafy.horong.domain.community.entity.Notification;
import ssafy.horong.domain.member.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverAndIsReadFalse(User receiver);
    List<Notification> findByReceiverAndIsReadFalseAndType(User receiver, Notification.NotificationType type);
    
    // 오래된 알림을 배치로 처리하기 위한 메서드
    @Query("SELECT n FROM Notification n WHERE n.createdAt < :date")
    List<Notification> findOldNotifications(@Param("date") LocalDateTime date, Pageable pageable);
}
