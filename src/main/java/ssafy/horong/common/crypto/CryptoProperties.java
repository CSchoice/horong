package ssafy.horong.common.crypto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 암호화 관련 설정 속성
 */
@Component
@ConfigurationProperties(prefix = "crypto")
@Getter
@Setter
@Validated
public class CryptoProperties {

    /**
     * 암호화에 사용되는 비밀키 (32바이트, 256비트)
     * application.yml 또는 환경 변수에서 설정
     */
    @NotBlank(message = "암호화 비밀키는 반드시 설정해야 합니다")
    @Size(min = 32, max = 32, message = "암호화 비밀키는 정확히 32자여야 합니다")
    private String secretKey;

    /**
     * 암호화 활성화 여부
     */
    private boolean enabled = true;
}
