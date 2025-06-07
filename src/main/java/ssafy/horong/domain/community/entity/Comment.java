package ssafy.horong.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import ssafy.horong.domain.common.BaseEntity;
import ssafy.horong.domain.common.SoftDeletable;
import ssafy.horong.domain.member.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity implements SoftDeletable {

    // 8바이트 필드와 날짜 필드는 BaseEntity에서 상속받음
    
    // 참조 타입 필드 (8바이트 참조)
    @ManyToOne
    @JoinColumn(name = "board_id")
    private Post board;
    
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<ContentByLanguage> contentByCountries;

    /**
     * 삭제 시간을 설정합니다.
     * SoftDeletable 인터페이스 구현
     * 
     * @param deletedAt 삭제 시간
     */
    @Override
    public void setDeletedAt(LocalDateTime deletedAt) {
        super.deletedAt = deletedAt;
    }
    
    /**
     * 삭제 시간을 가져옵니다.
     * SoftDeletable 인터페이스 구현
     * 
     * @return 삭제 시간
     */
    @Override
    public LocalDateTime getDeletedAt() {
        return super.deletedAt;
    }
    
    /**
     * 댓글을 논리적으로 삭제합니다.
     * SoftDeletable 인터페이스의 기본 구현을 사용합니다.
     */
    @Override
    public void softDelete() {
        SoftDeletable.super.softDelete();
    }
    
    /**
     * 댓글이 삭제되었는지 확인합니다.
     * 
     * @return 삭제되었으면 true, 아니면 false
     */
    @Override
    public boolean isDeleted() {
        return SoftDeletable.super.isDeleted();
    }
    
    /**
     * 댓글 삭제를 취소합니다.
     */
    public void restore() {
        this.undoDelete();
    }
}
