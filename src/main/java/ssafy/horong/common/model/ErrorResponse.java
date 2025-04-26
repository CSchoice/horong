package ssafy.horong.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * HTTP 에러 응답을 위한 표준 데이터 구조
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int error;
    private String message;
}
