package ssafy.horong.common.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static ssafy.horong.api.StatusCode.INTERNAL_SERVER_ERROR;
import static ssafy.horong.api.StatusCode.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum DataErrorCode implements BaseErrorCode { // BaseErrorCode를 상속
    DATA_NOT_FOUND(NOT_FOUND, "DATA_404_1", "데이터를 찾을 수 없습니다."),
    DATA_ENCRYPTION_ERROR(INTERNAL_SERVER_ERROR, "DATA_500_1", "데이터 암호화/복호화 중 오류가 발생했습니다.");

    private final Integer httpStatus; // HTTP 상태 코드
    private final String code;          // 에러 코드
    private final String message;       // 에러 메시지
}
