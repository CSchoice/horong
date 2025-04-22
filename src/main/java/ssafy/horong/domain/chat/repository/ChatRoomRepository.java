package ssafy.horong.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.horong.domain.chat.entity.ChatRoom;
import ssafy.horong.domain.member.entity.User;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByUser(User user);
}