package ssafy.horong.common.crypto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 문자열 필드를 자동으로 암호화/복호화하는 JPA 컨버터
 * 엔티티의 민감한 필드에 @Convert(converter = StringEncryptConverter.class) 어노테이션을 추가하여 사용
 */
@Slf4j
@Converter
@Component
@RequiredArgsConstructor
public class StringEncryptConverter implements AttributeConverter<String, String> {

    private final CryptoUtil cryptoUtil;
    private final CryptoProperties cryptoProperties;

    /**
     * 엔티티 저장 시 데이터베이스 컬럼으로 변환 (암호화)
     */
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || !cryptoProperties.isEnabled()) {
            return attribute;
        }
        
        try {
            return cryptoUtil.encrypt(attribute);
        } catch (Exception e) {
            log.error("필드 암호화 중 오류 발생: {}", e.getMessage(), e);
            // 암호화 실패 시 비암호화 상태로 저장 (데이터 손실 방지)
            return attribute;
        }
    }

    /**
     * 데이터베이스에서 조회 시 엔티티 필드로 변환 (복호화)
     */
    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || !cryptoProperties.isEnabled()) {
            return dbData;
        }
        
        try {
            return cryptoUtil.decrypt(dbData);
        } catch (Exception e) {
            log.error("필드 복호화 중 오류 발생: {}", e.getMessage(), e);
            // 복호화 실패 시 원본 데이터 반환 (UI에서 처리 가능)
            return dbData;
        }
    }
}
