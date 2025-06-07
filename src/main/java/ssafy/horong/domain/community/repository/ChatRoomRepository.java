package ssafy.horong.domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssafy.horong.domain.community.entity.ChatRoom;
import ssafy.horong.domain.community.entity.Post;
import ssafy.horong.domain.member.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * 채팅방 엔티티에 대한 데이터 액세스를 제공하는 리포지토리 인터페이스
 */
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /**
     * 특정 사용자가 참여한 모든 채팅방 조회
     * 
     * @param user 조회할 사용자
     * @return 사용자가 참여한 채팅방 목록
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.host = :user OR cr.guest = :user")
    List<ChatRoom> findAllByUser(@Param("user") User user);

    /**
     * 특정 게시물과 관련된 모든 채팅방 조회
     * 
     * @param post 조회할 게시물
     * @return 게시물과 관련된 채팅방 목록
     */
    List<ChatRoom> findAllByPost(Post post);

    /**
     * 특정 호스트와 게스트 사이의 채팅방 조회
     * 
     * @param host 호스트 사용자
     * @param guest 게스트 사용자
     * @return 호스트와 게스트 사이의 채팅방 (존재하는 경우)
     */
    Optional<ChatRoom> findByHostAndGuest(User host, User guest);

    /**
     * 특정 게시물과 사용자에 대한 채팅방 조회
     * 
     * @param post 게시물
     * @param user 사용자
     * @return 게시물과 사용자에 대한 채팅방 목록
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.post = :post AND (cr.host = :user OR cr.guest = :user)")
    List<ChatRoom> findAllByPostAndUser(@Param("post") Post post, @Param("user") User user);

    /**
     * 특정 게시물에 대한 특정 호스트와 게스트 사이의 채팅방 조회
     * 
     * @param post 게시물
     * @param host 호스트 사용자
     * @param guest 게스트 사용자
     * @return 조건에 맞는 채팅방 (존재하는 경우)
     */
    Optional<ChatRoom> findByPostAndHostAndGuest(Post post, User host, User guest);
    
    /**
     * 특정 채팅방 ID로 게시물 ID 조회
     * 
     * @param chatRoomId 채팅방 ID
     * @return 채팅방에 연결된 게시물 ID
     */
    @Query("SELECT cr.post.id FROM ChatRoom cr WHERE cr.id = :chatRoomId")
    Long findPostIdByChatRoomId(@Param("chatRoomId") Long chatRoomId);
}
