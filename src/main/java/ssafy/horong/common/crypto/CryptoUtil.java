package ssafy.horong.common.crypto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import ssafy.horong.common.exception.crypto.CryptoOperationException;

/**
 * 민감한 정보를 암호화하기 위한 유틸리티 클래스
 * AES-256-GCM 알고리즘을 사용하여 안전한 암호화 제공
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CryptoUtil {

    private final CryptoProperties cryptoProperties;
    private static final int GCM_IV_LENGTH = 12; // GCM 권장 IV 길이
    private static final int GCM_TAG_LENGTH = 16; // GCM 태그 길이 (비트 단위)

    /**
     * 문자열을 암호화합니다.
     * 
     * @param plainText 암호화할 평문
     * @return 암호화된 텍스트 (Base64 인코딩 문자열)
     */
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        
        try {
            // 비밀키 생성
            byte[] keyBytes = cryptoProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            
            // 무작위 IV(초기화 벡터) 생성
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            
            // GCM 파라미터 스펙 설정
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            
            // 암호화 초기화
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
            
            // 암호화 수행
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            // IV와 암호문을 결합 (IV는 암호문 앞에 추가)
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);
            
            // Base64 인코딩하여 반환
            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            log.error("암호화 과정에서 오류가 발생했습니다: {}", e.getMessage(), e);
            throw new CryptoOperationException("암호화에 실패했습니다", e);
        }
    }

    /**
     * 암호화된 텍스트를 복호화합니다.
     * 
     * @param encryptedText 암호화된 텍스트 (Base64 인코딩 문자열)
     * @return 복호화된 평문
     */
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }
        
        try {
            // Base64 디코딩
            byte[] cipherMessage = Base64.getDecoder().decode(encryptedText);
            
            // IV 추출
            ByteBuffer byteBuffer = ByteBuffer.wrap(cipherMessage);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);
            
            // 암호문 추출
            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);
            
            // 비밀키 생성
            byte[] keyBytes = cryptoProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            
            // GCM 파라미터 스펙 설정
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            
            // 복호화 초기화
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
            
            // 복호화 수행
            byte[] plainText = cipher.doFinal(cipherText);
            
            // 문자열로 변환하여 반환
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("복호화 과정에서 오류가 발생했습니다: {}", e.getMessage(), e);
            throw new CryptoOperationException("복호화에 실패했습니다", e);
        }
    }
}
