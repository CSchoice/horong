package ssafy.horong.domain.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ssafy.horong.domain.common.BaseEntity;
import ssafy.horong.domain.common.SoftDeletable;
import ssafy.horong.domain.member.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom extends BaseEntity implements SoftDeletable {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 채팅방과 채팅 메시지 간의 관계 설정 (CascadeType.ALL 추가)
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Chat> chatMessages = new ArrayList<>();

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
}