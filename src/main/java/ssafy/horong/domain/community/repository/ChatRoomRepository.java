package ssafy.horong.domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssafy.horong.domain.community.entity.ChatRoom;
import ssafy.horong.domain.member.entity.User;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("select c from ChatRoom c where c.host = :user or c.guest = :user")
    List<ChatRoom> findAllByUser(@Param("user") User user);

    @Query("SELECT c.id FROM ChatRoom c " +
            "WHERE c.post.id = :postId AND (c.host.id = :userId OR c.guest.id = :userId) AND (c.host.id = :loginedUser OR c.guest.id = :loginedUser)")
    Optional<Long> findChatRoomIdByUserAndPost(@Param("loginedUser") Long loginedUserId, @Param("userId") Long userId, @Param("postId") Long postId);

    @Query("SELECT c.post.id FROM ChatRoom c WHERE c.id = :chatRoomId")
    Long findPostIdByChatRoomId(@Param("chatRoomId") Long chatRoomId);
}
