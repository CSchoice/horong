package ssafy.horong.config.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import ssafy.horong.common.properties.RedisProperties;

/**
 * Rate Limiting 관련 설정 클래스 
 * 수정된 구현에서는 RedisTemplate을 사용하여 Redis에 접근하므로
 * 추가적인 Redis 클라이언트가 필요하지 않습니다.
 */
@Configuration
@RequiredArgsConstructor
public class RedisClientConfig {

    private final RedisProperties redisProperties;
}
