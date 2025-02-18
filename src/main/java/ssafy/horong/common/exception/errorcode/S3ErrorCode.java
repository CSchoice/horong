package ssafy.horong.common.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static ssafy.horong.api.StatusCode.BAD_REQUEST;
import static ssafy.horong.api.StatusCode.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum S3ErrorCode implements BaseErrorCode{
    EXTENSION_NOT_ALLOWED(BAD_REQUEST, "S3_400_1", "확장자는 jpg, jpeg, png, gif, mp3, wav만 가능합니다."),
    IMAGE_TOO_LARGE(BAD_REQUEST, "S3_400_2", "업로드할 용량이 2MB를 초과합니다."),
    S3_IMAGE_UPLOAD_FAILED(BAD_REQUEST, "S3_400_3", "업로드에 실패하였습니다."),
    PERSIGNEDURL_GENERATION_FAILED(BAD_REQUEST, "S3_400_4", "PreSignedURL 생성에 실패하였습니다."),

    PROFILE_NOT_FOUND_IN_S3(NOT_FOUND, "S3_404_1", "S3에 프로필 이미지를 찾을 수 없습니다.");

    private final Integer httpStatus;
    private final String code;
    private final String message;
}
