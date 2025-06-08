package ssafy.horong.domain.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import ssafy.horong.domain.common.BaseEntity;
import ssafy.horong.domain.common.SoftDeletable;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chat extends BaseEntity implements SoftDeletable {
    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthorType authorType;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private ChatRoom room;
    
    /**
     * SoftDeletable 인터페이스 구현
     * 엔티티의 삭제 시간을 설정합니다.
     * @param deletedAt 삭제 시간
     */
    @Override
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    
    /**
     * 엔티티를 논리적으로 삭제합니다.
     */
    @Override
    public void softDelete() {
        setDeletedAt(LocalDateTime.now());
    }
    
    /**
     * 엔티티가 삭제되었는지 확인합니다.
     * @return 삭제되었으면 true, 아니면 false
     */
    @Override
    public boolean isDeleted() {
        return getDeletedAt() != null;
    }
    
    /**
     * 삭제된 엔티티를 복원합니다.
     */
    @Override
    public void undoDelete() {
        setDeletedAt(null);
    }

    public enum AuthorType {
        USER, BOT
    }
}