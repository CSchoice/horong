package ssafy.horong.common.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static ssafy.horong.api.StatusCode.*;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
    USERID_DUPLICATE(BAD_REQUEST, "USER_400_1", "이미 사용 중인 ID입니다."),
    NICKNAME_DUPLICATE(BAD_REQUEST, "USER_400_2", "이미 사용 중인 닉네임입니다."),
    USER_DUPLICATE(BAD_REQUEST, "USER_400_3", "이미 가입된 회원입니다."),
    PASSWORD_NOT_MATCH(NOT_FOUND, "USER_400_5", "입력한 비밀번호가 일치하지 않습니다."),
    USERID_NOT_VALID(BAD_REQUEST, "USER_400_4", "유저 ID는 2자에서 최대 16자까지 가능합니다."),
    PASSWORD_NOT_VALID(BAD_REQUEST, "USER_400_6", "비밀번호는 8자 이상 20자 이하로 입력해야 합니다."),
    NICKNAME_NOT_VALID(BAD_REQUEST, "USER_400_7", "닉네임은 2자 이상 20자 이하로 입력해야 합니다."),
    NOT_ALLOWED_NICKNAME(BAD_REQUEST, "USER_400_9", "닉네임은 한글, 영어, 중국어, 일본어, 숫자만 사용 가능합니다."),
    NOT_ALLOWED_USERID(BAD_REQUEST, "USER_400_10", "유저 ID는 영문과 숫자만 입력 가능합니다."),
    LANGUAGE_NOT_VALID(BAD_REQUEST, "USER_400_8", "지정된 언어만 선택할 수 있습니다."),

    INVALID_LOGIN_INFO(UNAUTHORIZED, "USER_401_1", "로그인 정보를 찾을 수 없습니다."),

    USER_NOT_FOUND(NOT_FOUND, "USER_404_1", "회원 정보가 존재하지 않습니다."),
    PROFILE_NOT_FOUND(NOT_FOUND, "USER_404_3", "프로필 이미지를 찾을 수 없습니다."),
    VERIFICATION_FAILURE(NOT_FOUND, "USER_404_4", "인증에 실패하였습니다."),
    EMAIL_NOT_FOUND(NOT_FOUND, "USER_404_5", "이메일 주소가 존재하지 않습니다."),

    USER_ALREADY_DELETED(CONFLICT, "USER_409_1", "이미 삭제된 회원입니다."),
    FORBIDDEN_WORD_CONTAINED(CONFLICT, "USER_409_2", "ID 또는 닉네임에 금지어가 포함되어 있습니다."),

    ABNORMAL_LOGIN_PROGRESS(INTERNAL_SERVER_ERROR, "USER_500_1", "로그인 절차 중 오류가 발생했습니다.");

    private final Integer httpStatus;
    private final String code;
    private final String message;

    @Override
    public Integer getHttpStatus() {
        return this.httpStatus;
    }
}
