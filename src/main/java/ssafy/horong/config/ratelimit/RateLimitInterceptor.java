package ssafy.horong.config.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import ssafy.horong.common.ratelimit.RateLimiterService;
import ssafy.horong.common.ratelimit.RateLimiterService.RateLimitInfo;
import ssafy.horong.common.util.SecurityUtil;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimiterService rateLimiterService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 클라이언트 IP 주소 가져오기
        String clientIp = getClientIp(request);
        String path = request.getRequestURI();
        
        // 현재 인증된 사용자 ID (없으면 "anonymous")
        Optional<Long> userId = SecurityUtil.getLoginMemberId();
        String userIdStr = userId.map(String::valueOf).orElse("anonymous");

        // Rate Limit 검사
        boolean allowed = rateLimiterService.tryConsume(clientIp, path, userIdStr);
        
        if (!allowed) {
            // 요청 거부 응답 설정
            setRateLimitExceededResponse(response);
            return false;
        }
        
        // Rate Limit 정보 헤더 추가 (허용된 경우에도)
        addRateLimitHeaders(response, clientIp, path, userIdStr);
        
        return true;
    }

    private void setRateLimitExceededResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Too many requests\",\"message\":\"API 호출 한도를 초과했습니다. 잠시 후 다시 시도해주세요.\"}");
    }

    private void addRateLimitHeaders(HttpServletResponse response, String clientIp, String path, String userId) {
        RateLimitInfo info = rateLimiterService.getRateLimitInfo(clientIp, path, userId);
        
        response.addHeader("X-RateLimit-Limit", String.valueOf(info.getLimit()));
        response.addHeader("X-RateLimit-Remaining", String.valueOf(info.getRemaining()));
        response.addHeader("X-RateLimit-Reset", String.valueOf(info.getResetSeconds()));
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        final String UNKNOWN = "unknown";
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 쉼표로 구분된 여러 IP가 있는 경우 첫 번째 IP만 사용
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}
