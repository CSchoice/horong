package ssafy.horong.api.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;
import ssafy.horong.domain.member.command.UpdateProfileCommand;

@Schema(description = "유저 정보 변경 요청 DTO")
public record UserUpdateRequest(
        @Schema(description = "닉네임", example = "쿠잉비")
        String nickname

//        @Schema(description = "프로필 이미지 파일")
//        MultipartFile profileImagePath,
//
//        @Schema(description = "사진 삭제 여부", example = "true")
//        boolean deleteImage
) {
    public UpdateProfileCommand toCommand() {
        return new UpdateProfileCommand(nickname);
    }
}
