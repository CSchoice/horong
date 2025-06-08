package ssafy.horong.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import ssafy.horong.domain.common.BaseEntity;
import ssafy.horong.domain.common.SoftDeletable;
import ssafy.horong.domain.member.entity.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 메시지 엔티티 클래스
 * 채팅방 내의 메시지를 나타냅니다.
 * 메모리 효율성을 위해 필드 순서가 최적화되었습니다.
 */
@Entity
@Getter
@Setter
@ToString(exclude = {"contentByCountries"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Message extends BaseEntity implements SoftDeletable {
    /**
     * 메시지 고유 식별자와 날짜 필드는 BaseEntity에서 상속받음
     */
    
    /**
     * 메시지 작성자
     * 참조 타입 필드 (8바이트 참조)
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * 메시지가 속한 채팅방
     * 참조 타입 필드 (8바이트 참조)
     */
    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;
    
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
     * 메시지를 논리적으로 삭제합니다.
     * SoftDeletable 인터페이스의 기본 구현을 사용합니다.
     */
    @Override
    public void softDelete() {
        SoftDeletable.super.softDelete();
    }
    
    /**
     * 메시지가 삭제되었는지 확인합니다.
     * 
     * @return 삭제되었으면 true, 아니면 false
     */
    @Override
    public boolean isDeleted() {
        return SoftDeletable.super.isDeleted();
    }
    
    /**
     * 삭제된 메시지를 복구합니다.
     * SoftDeletable 인터페이스의 undoDelete 기본 구현을 사용합니다.
     */
    public void restore() {
        this.undoDelete();
    }
    
    /**
     * 메시지의 다국어 콘텐츠 목록
     */
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<ContentByLanguage> contentByCountries;
    
    /**
     * 메시지 읽기 여부
     * 1바이트 필드 (마지막에 배치하여 패딩 최소화)
     */
    private boolean isRead;

    /**
     * 필드 초기화를 위한 생성자
     */
    @Builder
    public Message(User user, ChatRoom chatRoom, List<ContentByLanguage> contentByCountries) {
        this.user = user;
        this.chatRoom = chatRoom;
        this.contentByCountries = contentByCountries;
        this.isRead = false;
    }

    public void readMessage() {
        this.isRead = true;
    }

    /**
     * 메시지 타입을 나타내는 열거형
     */
    public enum UserMessageType {
        /** 현재 사용자가 보낸 메시지 */
        USER, 
        /** 상대방이 보낸 메시지 */
        OPPONENT
    }
}
