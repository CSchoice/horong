package ssafy.horong.common.exception.crypto;

import ssafy.horong.common.exception.BaseException;
import ssafy.horong.common.exception.errorcode.DataErrorCode;

/**
 * 암호화/복호화 작업 중 발생하는 예외를 처리하는 클래스
 */
public class CryptoOperationException extends BaseException {
    
    public CryptoOperationException(String message) {
        super(DataErrorCode.DATA_ENCRYPTION_ERROR, message);
    }
    
    public CryptoOperationException(String message, Throwable cause) {
        super(DataErrorCode.DATA_ENCRYPTION_ERROR, message);
        initCause(cause);
    }
}
