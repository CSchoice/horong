package ssafy.horong.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.common.exception.board.*;

@RestControllerAdvice
@Slf4j
public class CommunityExceptionHandler {

    @ExceptionHandler(NotAdminException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResponse<Void>handleNotAdminException(NotAdminException e) {
        log.error("NotAdminExeption", e);
        return CommonResponse.unauthorized(e.getErrorCode());
    }

    @ExceptionHandler(NotAuthenticatedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResponse<Void>handleNotAuthenticatedException(NotAuthenticatedException e) {
        log.error("NotAuthenticatedException", e);
        return CommonResponse.unauthorized(e.getErrorCode());
    }

    @ExceptionHandler(ContentTooLongException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void>handleContentTooLongException(ContentTooLongException e) {
        log.error("ContentTooLongExeption", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse<Void>handlePostNotFoundException(PostNotFoundException e) {
        log.error("PostNotFoundException", e);
        return CommonResponse.notFound(e.getErrorCode());
    }

    @ExceptionHandler(PostDeletedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse<Void>handlePostDeletedException(PostDeletedException e) {
        log.error("PostDeletedException", e);
        return CommonResponse.notFound(e.getErrorCode());
    }

    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse<Void>handleCommentNotFoundException(CommentNotFoundException e) {
        log.error("CommentNotFoundException", e);
        return CommonResponse.notFound(e.getErrorCode());
    }

    @ExceptionHandler(MessageRoomNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse<Void>handleChatRoomNotFoundException(MessageRoomNotFoundException e) {
        log.error("ChatRoomNotFoundException", e);
        return CommonResponse.notFound(e.getErrorCode());
    }
}
