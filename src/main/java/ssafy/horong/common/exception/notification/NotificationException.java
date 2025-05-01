package ssafy.horong.common.exception.notification;

/**
 * 알림 처리 중 발생하는 예외를 처리하기 위한 전용 예외 클래스
 */
public class NotificationException extends RuntimeException {
    
    public NotificationException(String message) {
        super(message);
    }
    
    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
