package ssafy.horong.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ssafy.horong.config.ratelimit.RateLimitInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/**") // Rate Limiting을 적용할 경로 패턴
                .excludePathPatterns(
                        "/api/health/**", 
                        "/swagger-ui/**", 
                        "/v3/api-docs/**", 
                        "/error"
                ); // 제외할 경로
    }
}
