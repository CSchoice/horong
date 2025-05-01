package ssafy.horong.domain.community.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssafy.horong.domain.community.entity.MessageRoom;
import ssafy.horong.domain.community.entity.Message;
import ssafy.horong.domain.member.entity.User;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("select m from Message m where m.messageRoom.id = :messageRoomId")
    List<Message> findAllByMessageRoomId(@Param("messageRoomId") Long messageRoomId);

    List<Message> findAllByUser(User user);

    // 읽지 않은 메시지 카운트 - 방 ID와 사용자 ID로 조회
    @Query("SELECT COUNT(m) FROM Message m WHERE m.messageRoom.id = :roomId AND m.isRead = false AND m.user.id <> :userId")
    long countUnreadMessagesForOpponent(@Param("roomId") Long roomId, @Param("userId") Long userId);
    
    // 읽지 않은 메시지 카운트 - 방과 사용자 객체로 조회
    @Query("SELECT COUNT(m) FROM Message m WHERE m.messageRoom = :messageRoom AND m.isRead = false AND m.user <> :user")
    long countUnreadMessagesForOpponentByEntities(@Param("messageRoom") MessageRoom messageRoom, @Param("user") User user);

    @Query("SELECT CASE WHEN c.host.id = :myId THEN c.guest.id ELSE c.host.id END " +
            "FROM MessageRoom c WHERE c.id = :messageRoomId")
    Long findOpponentIdByMessageRoomIdAndUserId(@Param("messageRoomId") Long messageRoomId, @Param("myId") Long myId);
    
    // 최신 메시지만 조회하는 메서드 추가
    @Query("SELECT m FROM Message m WHERE m.messageRoom.id = :roomId ORDER BY m.createdAt DESC")
    Page<Message> findLatestMessageByRoomId(@Param("roomId") Long roomId, Pageable pageable);
}