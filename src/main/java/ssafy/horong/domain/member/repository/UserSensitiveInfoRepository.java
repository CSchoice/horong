package ssafy.horong.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.entity.UserSensitiveInfo;

import java.util.Optional;

/**
 * 사용자 민감 정보 저장소
 */
@Repository
public interface UserSensitiveInfoRepository extends JpaRepository<UserSensitiveInfo, Long> {
    
    /**
     * 사용자 ID로 민감 정보 조회
     */
    Optional<UserSensitiveInfo> findByUser(User user);
    
    /**
     * 사용자 ID로 민감 정보 존재 여부 확인
     */
    boolean existsByUser(User user);
}
