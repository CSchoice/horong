package ssafy.horong.domain.common;

import java.time.LocalDateTime;

/**
 * 논리적 삭제(Soft Delete)를 지원하는 엔티티를 위한 인터페이스
 * 이 인터페이스를 구현하는 엔티티는 물리적으로 삭제되지 않고 논리적으로만 삭제됨
 */
public interface SoftDeletable {
    
    /**
     * 엔티티의 삭제 시간을 반환
     * @return 삭제 시간, 삭제되지 않은 경우 null
     */
    LocalDateTime getDeletedAt();
    
    /**
     * 엔티티의 삭제 시간을 설정
     * @param deletedAt 삭제 시간
     */
    void setDeletedAt(LocalDateTime deletedAt);
    
    /**
     * 엔티티를 논리적으로 삭제
     * 현재 시간을 삭제 시간으로 설정
     */
    default void softDelete() {
        setDeletedAt(LocalDateTime.now());
    }
    
    /**
     * 엔티티가 삭제되었는지 확인
     * @return 삭제 여부
     */
    default boolean isDeleted() {
        return getDeletedAt() != null;
    }
    
    /**
     * 엔티티의 삭제를 취소
     * 삭제 시간을 null로 설정
     */
    default void undoDelete() {
        setDeletedAt(null);
    }
}
