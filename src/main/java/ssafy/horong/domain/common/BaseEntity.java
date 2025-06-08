package ssafy.horong.domain.common;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 모든 엔티티에서 공통으로 사용되는 기본 필드와 메서드를 정의한 추상 클래스
 * 메모리 효율성을 위해 필드 순서 최적화 (8바이트 필드 → 날짜 필드 → 기타 필드)
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    // 8바이트 필드
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    // 날짜 필드 (8바이트)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @LastModifiedDate
    protected LocalDateTime updatedAt;
    
    protected LocalDateTime deletedAt;

    /**
     * 엔티티 생성 전 호출되는 메서드
     * 생성 시간을 현재 시간으로 설정
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * 엔티티 수정 전 호출되는 메서드
     * 수정 시간을 현재 시간으로 설정
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 논리적 삭제를 위한 메서드
     * 삭제 시간을 현재 시간으로 설정
     */
    public void markAsDeleted() {
        deletedAt = LocalDateTime.now();
    }

    /**
     * 엔티티가 삭제되었는지 확인하는 메서드
     * @return 삭제 여부
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }
}
