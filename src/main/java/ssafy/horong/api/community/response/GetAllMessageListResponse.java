package ssafy.horong.api.community.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetAllMessageListResponse(
        @Schema(description = "메시지 수", example = "1")
        Long messageCount,

        @Schema(description = "마지막 메시지 내용", example = "안녕하세요")
        String content,

        @Schema(description = "전송자 닉네임", example = "홍길동")
        String senderNickname,

        @Schema(description = "전송자 id", example = "1")
        Long senderId,

        @Schema(description = "마지막 전송 시간", example = "2021-07-01T00:00:00")
        String createdAt
) {
    public GetAllMessageListResponse of(Long messageCount, String content, String senderNickname, Long senderId, String createdAt) {
        return new GetAllMessageListResponse(messageCount, content, senderNickname, senderId, createdAt);
    }
}
