package ssafy.horong.common.ratelimit;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ssafy.horong.config.ratelimit.RateLimitProperties;
import ssafy.horong.config.ratelimit.RateLimitProperties.RateLimitRule;
import ssafy.horong.config.ratelimit.RateLimitProperties.RateLimitType;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final RateLimitProperties rateLimitProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void initialize() {
        try {
            log.info("Rate Limiter가 초기화되었습니다.");
        } catch (Exception e) {
            log.error("Rate Limiter 초기화 중 오류가 발생했습니다: {}", e.getMessage(), e);
        }
    }

    public boolean tryConsume(String key, String path, String userId) {
        if (!rateLimitProperties.isEnabled()) {
            return true; // Rate Limiting 비활성화 상태면 항상 허용
        }

        // 경로에 맞는 적절한 Rate Limit 규칙 찾기
        RateLimitRule rule = findRuleForPath(path);
        if (rule == null) {
            return true; // 해당 경로에 대한 규칙이 없으면 허용
        }

        // 키 생성 (규칙 타입에 따라 다름)
        String bucketKey = generateBucketKey(rule, key, userId);

        try {
            // Redis에 요청 횟수 저장
            String redisKey = "ratelimit:" + bucketKey;
            Long currentCount = redisTemplate.opsForValue().increment(redisKey, 1);
            
            // TTL 설정 (처음 접근하는 경우에만)
            if (currentCount != null && currentCount == 1) {
                redisTemplate.expire(redisKey, Duration.ofSeconds(rule.getRefillDurationSec()));
            }
            
            // 요청 허용 판단
            boolean consumed = currentCount != null && currentCount <= rule.getCapacity();
            if (!consumed) {
                log.warn("Rate limit 초과: key={}, path={}, count={}", bucketKey, path, currentCount);
            }
            return consumed;
        } catch (Exception e) {
            log.error("Rate limit 검사 중 오류 발생: {}", e.getMessage(), e);
            return true; // 오류 발생 시 요청 허용 (서비스 가용성 우선)
        }
    }

    private String generateBucketKey(RateLimitRule rule, String ipAddress, String userId) {
        switch (rule.getType()) {
            case IP:
                return String.format("%s:%s", rule.getName(), ipAddress);
            case USER:
                return String.format("%s:user:%s", rule.getName(), userId);
            case API_KEY:
                return String.format("%s:apikey:%s", rule.getName(), ipAddress);
            default:
                return String.format("%s:%s", rule.getName(), ipAddress);
        }
    }

    private RateLimitRule findRuleForPath(String path) {
        return rateLimitProperties.getRules().stream()
                .filter(rule -> pathMatches(path, rule.getPathPattern()))
                .findFirst()
                .orElse(null);
    }

    private boolean pathMatches(String path, String pattern) {
        // 간단한 경로 패턴 매칭 로직
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        } else if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            String[] pathParts = path.split("/");
            String[] prefixParts = prefix.split("/");
            
            if (pathParts.length != prefixParts.length + 1) {
                return false;
            }
            
            for (int i = 0; i < prefixParts.length; i++) {
                if (!prefixParts[i].equals(pathParts[i])) {
                    return false;
                }
            }
            
            return true;
        }
        return path.equals(pattern);
    }

    // Rate Limit 정보 반환 (HTTP 헤더용)
    public RateLimitInfo getRateLimitInfo(String key, String path, String userId) {
        RateLimitRule rule = findRuleForPath(path);
        if (rule == null) {
            return new RateLimitInfo(0, 0, 0);
        }

        String bucketKey = generateBucketKey(rule, key, userId);
        String redisKey = "ratelimit:" + bucketKey;
        
        // 현재 사용량 조회
        Object value = redisTemplate.opsForValue().get(redisKey);
        long currentCount = (value != null) ? Long.parseLong(value.toString()) : 0;
        
        // 남은 시간 조회
        Long ttl = redisTemplate.getExpire(redisKey);
        long remainingSeconds = (ttl != null && ttl > 0) ? ttl : rule.getRefillDurationSec();
        
        return new RateLimitInfo(
                rule.getCapacity(),
                Math.max(0, rule.getCapacity() - currentCount),
                remainingSeconds
        );
    }

    // Rate Limit 정보를 담는 클래스
    public static class RateLimitInfo {
        private final long limit;
        private final long remaining;
        private final long resetSeconds;

        public RateLimitInfo(long limit, long remaining, long resetSeconds) {
            this.limit = limit;
            this.remaining = remaining;
            this.resetSeconds = resetSeconds;
        }

        public long getLimit() {
            return limit;
        }

        public long getRemaining() {
            return remaining;
        }

        public long getResetSeconds() {
            return resetSeconds;
        }
    }
}
