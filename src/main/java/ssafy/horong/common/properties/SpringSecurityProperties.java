package ssafy.horong.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.user")
public record SpringSecurityProperties(
        String name,
        String password,
        String roles
) {
}
