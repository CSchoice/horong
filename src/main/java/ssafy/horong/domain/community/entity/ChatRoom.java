package ssafy.horong.domain.community.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ssafy.horong.domain.member.entity.User;

import java.util.List;

/**
 * 채팅방 엔티티 클래스
 * 사용자들 간의 메시지 교환을 위한 채팅방을 나타냅니다.
 * 메모리 효율성을 위해 필드 순서가 최적화되었습니다.
 */
@Entity
@Getter
@ToString(exclude = {"messages"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ChatRoom {
    /**
     * 채팅방 고유 식별자
     * 8바이트 필드
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
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
}
