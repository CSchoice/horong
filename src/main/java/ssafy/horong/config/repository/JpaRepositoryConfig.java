package ssafy.horong.config.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA 저장소 설정
 * JPA 저장소로 사용할 패키지를 명시적으로 지정하여 Spring Data Redis와의 충돌을 방지합니다.
 */
@Configuration
@EnableJpaRepositories(basePackages = {
    "ssafy.horong.domain.community.repository",
    "ssafy.horong.domain.currency.repository",
    "ssafy.horong.domain.education.repository",
    "ssafy.horong.domain.member.repository"
    // 여기에 다른 JPA 저장소 패키지를 추가하세요
})
public class JpaRepositoryConfig {
    // JPA 관련 추가 설정이 필요한 경우 여기에 작성
}
