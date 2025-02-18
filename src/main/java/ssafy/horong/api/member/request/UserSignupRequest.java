package ssafy.horong.api.member.request;

import ssafy.horong.domain.member.common.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.horong.domain.member.command.MemberSignupCommand;

@Schema(description = "유저 회원가입 요청 DTO")
public record UserSignupRequest(
        @Schema(description = "닉네임", example = "쿠잉비", minimum = "2", maximum = "20")
        String nickname,

        @Schema(description = "비밀번호", example = "password123!")
        String password,

        @Schema(description = "유저id", example = "test1")
        String userId,

        @Schema(description = "언어", example = "KOREAN")
        Language language
){
        public MemberSignupCommand toCommand() {
                return new MemberSignupCommand(nickname, password, userId, language);
        }
}
