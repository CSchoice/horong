package ssafy.sera.domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.sera.domain.community.entity.Post;

public interface BoardRepository extends JpaRepository<Post, Long> {
}
