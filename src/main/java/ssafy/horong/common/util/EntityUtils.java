package ssafy.horong.common.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ssafy.horong.domain.common.BaseEntity;
import ssafy.horong.domain.common.SoftDeletable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 엔티티 조작을 위한 유틸리티 클래스
 * 엔티티 관련 공통 작업을 처리하는 메서드 제공
 */
@Component
public class EntityUtils {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 엔티티가 영속 상태인지 확인
     * @param entity 확인할 엔티티
     * @return 영속 상태 여부
     */
    public boolean isPersistent(Object entity) {
        return entityManager.contains(entity);
    }

    /**
     * 엔티티를 새로고침하여 최신 상태로 업데이트
     * @param entity 새로고침할 엔티티
     * @param <T> 엔티티 타입
     */
    public <T> void refresh(T entity) {
        if (isPersistent(entity)) {
            entityManager.refresh(entity);
        }
    }

    /**
     * 엔티티 컬렉션을 안전하게 처리
     * null인 경우 빈 리스트 반환
     * @param collection 처리할 컬렉션
     * @param <T> 컬렉션 요소 타입
     * @return 안전한 컬렉션 (null이 아님)
     */
    public static <T> List<T> safeList(Collection<T> collection) {
        return Optional.ofNullable(collection)
                .map(List::copyOf)
                .orElse(List.of());
    }

    /**
     * 엔티티 컬렉션을 변환
     * @param collection 변환할 컬렉션
     * @param mapper 변환 함수
     * @param <T> 원본 타입
     * @param <R> 변환 결과 타입
     * @return 변환된 리스트
     */
    public static <T, R> List<R> mapEntities(Collection<T> collection, Function<T, R> mapper) {
        return safeList(collection).stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    /**
     * 엔티티 컬렉션에 대해 작업 수행
     * @param collection 작업을 수행할 컬렉션
     * @param action 수행할 작업
     * @param <T> 컬렉션 요소 타입
     */
    public static <T> void forEachEntity(Collection<T> collection, Consumer<T> action) {
        safeList(collection).forEach(action);
    }

    /**
     * BaseEntity 컬렉션에서 ID 리스트 추출
     * @param entities ID를 추출할 엔티티 컬렉션
     * @return ID 리스트
     */
    public static List<Long> extractIds(Collection<? extends BaseEntity> entities) {
        return safeList(entities).stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toList());
    }

    /**
     * SoftDeletable 엔티티 컬렉션을 논리적으로 삭제
     * @param entities 삭제할 엔티티 컬렉션
     */
    @Transactional
    public static void softDeleteAll(Collection<? extends SoftDeletable> entities) {
        forEachEntity(entities, SoftDeletable::softDelete);
    }

    /**
     * 두 엔티티 컬렉션의 차이를 계산
     * @param original 원본 컬렉션
     * @param updated 업데이트된 컬렉션
     * @param idExtractor ID 추출 함수
     * @param <T> 엔티티 타입
     * @param <ID> ID 타입
     * @return 추가된 항목, 제거된 항목, 유지된 항목을 포함하는 EntityDiff 객체
     */
    public static <T, ID> EntityDiff<T, ID> diff(
            Collection<T> original,
            Collection<T> updated,
            Function<T, ID> idExtractor) {
        
        List<T> originalList = safeList(original);
        List<T> updatedList = safeList(updated);
        
        List<ID> originalIds = originalList.stream()
                .map(idExtractor)
                .collect(Collectors.toList());
        
        List<ID> updatedIds = updatedList.stream()
                .map(idExtractor)
                .collect(Collectors.toList());
        
        List<T> added = updatedList.stream()
                .filter(item -> !originalIds.contains(idExtractor.apply(item)))
                .collect(Collectors.toList());
        
        List<T> removed = originalList.stream()
                .filter(item -> !updatedIds.contains(idExtractor.apply(item)))
                .collect(Collectors.toList());
        
        List<T> retained = originalList.stream()
                .filter(item -> updatedIds.contains(idExtractor.apply(item)))
                .collect(Collectors.toList());
        
        return new EntityDiff<>(added, removed, retained);
    }

    /**
     * 엔티티 컬렉션의 차이를 나타내는 클래스
     * @param <T> 엔티티 타입
     * @param <ID> ID 타입
     */
    public static class EntityDiff<T, ID> {
        private final List<T> added;
        private final List<T> removed;
        private final List<T> retained;
        
        public EntityDiff(List<T> added, List<T> removed, List<T> retained) {
            this.added = added;
            this.removed = removed;
            this.retained = retained;
        }
        
        public List<T> getAdded() {
            return added;
        }
        
        public List<T> getRemoved() {
            return removed;
        }
        
        public List<T> getRetained() {
            return retained;
        }
    }
}
