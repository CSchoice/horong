package ssafy.horong.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.horong.domain.chat.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {

}
