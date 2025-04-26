package ssafy.horong.common.crypto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 암호화된 필드 검색을 위한 도우미 클래스
 * 암호화된 필드는 데이터베이스에서 직접 검색할 수 없으므로
 * 메모리에서 복호화하여 검색하는 기능을 제공합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CryptoSearchHelper {

    private final CryptoUtil cryptoUtil;

    /**
     * 암호화된 필드를 기준으로 엔티티 목록을 필터링합니다.
     *
     * @param entities 엔티티 목록
     * @param fieldExtractor 엔티티에서 암호화된 필드를 추출하는 함수
     * @param searchValue 검색할 값
     * @param <T> 엔티티 타입
     * @return 검색 조건과 일치하는 엔티티 목록
     */
    public <T> List<T> filterByEncryptedField(
            List<T> entities,
            Function<T, String> fieldExtractor,
            String searchValue) {
        
        if (searchValue == null || searchValue.isEmpty()) {
            return entities;
        }
        
        return entities.stream()
                .filter(entity -> {
                    try {
                        String encryptedField = fieldExtractor.apply(entity);
                        if (encryptedField == null) {
                            return false;
                        }
                        
                        String decryptedField = cryptoUtil.decrypt(encryptedField);
                        return decryptedField.contains(searchValue);
                    } catch (Exception e) {
                        log.error("암호화된 필드 검색 중 오류 발생: {}", e.getMessage(), e);
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 암호화된 필드를 기준으로 페이징된 엔티티 목록을 필터링합니다.
     *
     * @param entities 모든 엔티티 목록
     * @param fieldExtractor 엔티티에서 암호화된 필드를 추출하는 함수
     * @param searchValue 검색할 값
     * @param pageable 페이징 정보
     * @param <T> 엔티티 타입
     * @return 검색 조건과 일치하는 페이징된 엔티티 목록
     */
    public <T> Page<T> filterByEncryptedFieldWithPaging(
            List<T> entities,
            Function<T, String> fieldExtractor,
            String searchValue,
            Pageable pageable) {
        
        List<T> filteredList = filterByEncryptedField(entities, fieldExtractor, searchValue);
        
        int start = (int) Math.min(pageable.getOffset(), filteredList.size());
        int end = Math.min(start + pageable.getPageSize(), filteredList.size());
        
        return new PageImpl<>(
                filteredList.subList(start, end),
                pageable,
                filteredList.size());
    }
}
