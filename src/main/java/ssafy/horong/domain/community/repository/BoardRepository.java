package ssafy.horong.domain.community.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssafy.horong.domain.community.entity.BoardType;
import ssafy.horong.domain.community.entity.Post;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Post, Long> {
    Page<Post> findByType(BoardType type, Pageable pageable);
    
    // 삭제되지 않은 게시글만 조회
    Page<Post> findByTypeAndDeletedAtIsNull(BoardType type, Pageable pageable);
    
    List<Post> findByTypeOrderByCreatedAtDesc(BoardType boardType, Pageable pageable);
    
    // N+1 문제 해결을 위한 조인 쿼리
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.author LEFT JOIN FETCH p.contentByCountries " +
           "WHERE p.type = :type AND p.deletedAt IS NULL ORDER BY p.createdAt DESC")
    List<Post> findByTypeWithAuthorAndContent(@Param("type") BoardType type, Pageable pageable);
    
    // 데이터 무결성을 위한 락 메커니즘 적용
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Post p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Post> findByIdWithPessimisticLock(@Param("id") Long id);
}
