package ssafy.horong.domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssafy.horong.domain.community.entity.MessageRoom;
import ssafy.horong.domain.member.entity.User;

import java.util.List;
import java.util.Optional;

public interface MessageRoomRepository extends JpaRepository<MessageRoom, Long> {
    @Query("select c from MessageRoom c where c.host = :user or c.guest = :user")
    List<MessageRoom> findAllByUser(@Param("user") User user);

    @Query("SELECT c.id FROM MessageRoom c " +
            "WHERE c.post.id = :postId AND (c.host.id = :userId OR c.guest.id = :userId) AND (c.host.id = :loginedUser OR c.guest.id = :loginedUser)")
    Optional<Long> findChatRoomIdByUserAndPost(@Param("loginedUser") Long loginedUserId, @Param("userId") Long userId, @Param("postId") Long postId);

    @Query("SELECT c.post.id FROM MessageRoom c WHERE c.id = :messageRoomId")
    Long findPostIdByMessageRoomId(@Param("messageRoomId") Long messageRoomId);
}
