package ssafy.horong.domain.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

import static ssafy.horong.common.constant.redis.KEY_PREFIX.REFRESH_TOKEN;

@Component
@Slf4j
public class RefreshTokenRedisRepository extends BaseRedisRepository<String> {
    public RefreshTokenRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.prefix = REFRESH_TOKEN;
        this.ttl = 60L * 60L * 24L * 30L; // 30 days
        this.redisTemplate = redisTemplate;
    }
    
    /**
     * 특정 사용자 ID를 값으로 가지는 모든 리프레시 토큰 키를 찾습니다.
     * @param userId 사용자 ID
     * @return 해당 사용자의 모든 리프레시 토큰 키 목록
     */
    public Set<String> findKeysByValue(String userId) {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        Set<String> userTokens = new java.util.HashSet<>();
        
        if (keys != null) {
            for (String key : keys) {
                String value = redisTemplate.opsForValue().get(key);
                if (userId.equals(value)) {
                    // 키에서 접두사 제거하여 실제 토큰값만 반환
                    userTokens.add(key.substring(prefix.length()));
                }
            }
        }
        return userTokens;
    }
}
