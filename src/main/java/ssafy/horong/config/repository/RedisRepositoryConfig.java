package ssafy.horong.config.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/**
 * Redis 저장소 설정
 * Redis 저장소로 사용할 패키지를 명시적으로 지정하여 Spring Data JPA와의 충돌을 방지합니다.
 */
@Configuration
@EnableRedisRepositories(basePackages = "ssafy.horong.domain.redis")
public class RedisRepositoryConfig {
    // Redis Repository 관련 추가 설정이 필요한 경우 여기에 작성
}
