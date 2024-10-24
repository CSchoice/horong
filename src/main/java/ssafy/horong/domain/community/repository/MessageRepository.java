package ssafy.horong.domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ssafy.horong.domain.community.entity.Message;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("select m from Message m where m.sender.id = :senderId or m.recipient.id = :recipientId")
    List<Message> findBySenderIdAndRecipient(Long senderId, Long recipientId);
}
