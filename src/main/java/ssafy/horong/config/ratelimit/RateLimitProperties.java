package ssafy.horong.config.ratelimit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "rate-limit")
@Getter
@Setter
public class RateLimitProperties {

    private boolean enabled = true;
    private List<RateLimitRule> rules = new ArrayList<>();

    @Getter
    @Setter
    public static class RateLimitRule {
        private String name;             // 규칙 이름
        private String pathPattern;      // 경로 패턴 (예: /api/v1/**)
        private int capacity;            // 버킷 용량 (최대 요청 수)
        private int refillTokens;        // 보충 토큰 수
        private int refillDurationSec;   // 보충 주기 (초)
        private RateLimitType type = RateLimitType.IP;  // Rate Limit 타입 (IP, USER, API_KEY)
    }

    public enum RateLimitType {
        IP,         // IP 주소 기반
        USER,       // 인증된 사용자 ID 기반
        API_KEY     // API 키 기반
    }
}
