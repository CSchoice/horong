package ssafy.horong.domain.community.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ssafy.horong.domain.common.BaseEntity;
import ssafy.horong.domain.common.SoftDeletable;
import ssafy.horong.domain.member.entity.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 채팅방 엔티티 클래스
 * 사용자들 간의 메시지 교환을 위한 채팅방을 나타냅니다.
 * 메모리 효율성을 위해 필드 순서가 최적화되었습니다.
 */
@Entity
@Getter
@Setter
@ToString(exclude = {"messages"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ChatRoom extends BaseEntity implements SoftDeletable {
    /**
     * 채팅방 고유 식별자와 날짜 필드는 BaseEntity에서 상속받음
     */
    
    /**
     * 채팅방이 연관된 게시물
     * 참조 타입 필드 (8바이트 참조)
     */
    @ManyToOne
    private Post post;
    
    
    /**
     * 채팅방 호스트 사용자
     */
    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private User host;
    
    /**
     * 채팅방 게스트 사용자
     */
    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false)
    private User guest;

    /**
     * 채팅방에 속한 메시지 목록
     */
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<Message> messages;
    
    /**
     * 채팅방 생성, 수정, 삭제 시간은 BaseEntity에서 상속받음
     */

    /**
     * 현재 사용자가 호스트 또는 게스트인지 확인하고,
     * 상대방을 반환하는 메서드.
     *
     * @param currentUser 현재 사용자
     * @return 상대방 사용자
     */
    public User getOpponent(User currentUser) {
        if (currentUser.equals(host)) {
            return guest;
        } else if (currentUser.equals(guest)) {
            return host;
        } else {
            return host;
        }
    }

    /**
     * 현재 사용자가 호스트 또는 게스트인지 확인하고,
     * 해당 사용자가 채팅방에 속해 있는지 확인합니다.
     *
     * @param userId 확인할 사용자 ID
     * @return 사용자가 채팅방에 속해 있으면 true, 아니면 false
     */
    public boolean isUserInChatRoom(Long userId) {
        return host.getId().equals(userId) || guest.getId().equals(userId);
    }
    
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
     * 채팅방을 논리적으로 삭제합니다.
     * SoftDeletable 인터페이스의 기본 구현을 사용합니다.
     */
    @Override
    public void softDelete() {
        SoftDeletable.super.softDelete();
    }
    
    /**
     * 채팅방이 삭제되었는지 확인합니다.
     * 
     * @return 삭제되었으면 true, 아니면 false
     */
    @Override
    public boolean isDeleted() {
        return SoftDeletable.super.isDeleted();
    }
    
    /**
     * 채팅방 삭제를 취소합니다.
     * SoftDeletable 인터페이스의 undoDelete 기본 구현을 사용합니다.
     */
    public void restore() {
        this.undoDelete();
    }
}
