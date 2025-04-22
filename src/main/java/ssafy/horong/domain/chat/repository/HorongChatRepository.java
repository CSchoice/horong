package ssafy.horong.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.horong.domain.chat.entity.HorongChat;

public interface HorongChatRepository extends JpaRepository<HorongChat, Long> {

}
