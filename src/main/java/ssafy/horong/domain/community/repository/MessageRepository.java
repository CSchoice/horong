package ssafy.horong.domain.community.repository;

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

    @Query("SELECT COUNT(m) FROM Message m WHERE m.messageRoom = :messageRoom AND m.isRead = false AND m.user <> :user")
    long countUnreadMessagesForOpponent(@Param("messageRoom") MessageRoom messageRoom, @Param("user") User user);

    @Query("SELECT CASE WHEN c.host.id = :myId THEN c.guest.id ELSE c.host.id END " +
            "FROM MessageRoom c WHERE c.id = :messageRoomId")
    Long findOpponentIdByMessageRoomIdAndUserId(@Param("messageRoomId") Long messageRoomId, @Param("myId") Long myId);
}