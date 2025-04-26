package ssafy.horong.domain.community.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메시지룸 정보를 효율적으로 조회하기 위한 DTO 클래스
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageRoomInfo {
    private Long roomId;
    private Long opponentId;
    private String opponentNickname;
    private String opponentProfileImg;
    private Long postId;
}
