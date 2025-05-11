package ssafy.horong.domain.community.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
@ToString(exclude = {"contentByCountries"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Message {
    /**
     * 메시지 고유 식별자
     * 8바이트 필드
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 메시지 생성 시간
     * 날짜 필드 (8바이트)
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * 메시지 작성자
     * 참조 타입 필드 (8바이트 참조)
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * 메시지가 속한 채팅방
     */
    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;
    
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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        isRead = false;
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
