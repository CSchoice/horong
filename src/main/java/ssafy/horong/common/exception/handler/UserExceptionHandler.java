package ssafy.horong.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.common.exception.user.*;

@RestControllerAdvice
@Slf4j
public class UserExceptionHandler {
    @ExceptionHandler(AbnormalLoginProgressException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse<Void>handleAbnormalLoginProgressException(AbnormalLoginProgressException e) {
        log.error("AbnormalLoginProgressException Error", e);
        return CommonResponse.internalServerError(e.getErrorCode());
    }

    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse<Void>handleNotFoundMemberException(MemberNotFoundException e) {
        log.error("MemberNotFoundException Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(UserAlreadyDeletedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public CommonResponse<Void>handleMemberAlreadyDeletedException(UserAlreadyDeletedException e) {
        log.error("UserAlreadyDeletedException Error", e);
        return CommonResponse.conflict(e.getErrorCode());
    }

    @ExceptionHandler(VerificationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse<Void>handleSmsVerificationException(VerificationException e) {
        log.error("VerificationException Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse<Void>handleEmailNotFoundException(EmailNotFoundException e) {
        log.error("EmailNotFoundException Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse<Void>handlePasswordNotMatchException(PasswordNotMatchException e) {
        log.error("PasswordNotMatchException Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(UserIdDuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public CommonResponse<Void>handleEmailDuplicateException(UserIdDuplicateException e) {
        log.error("UserIdDuplicateException Error", e);
        return CommonResponse.conflict(e.getErrorCode());
    }

    @ExceptionHandler(NickNameDuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public CommonResponse<Void>handleNickNameDuplicateException(NickNameDuplicateException e) {
        log.error("NickNameDuplicateException Error", e);
        return CommonResponse.conflict(e.getErrorCode());
    }

    @ExceptionHandler(PasswordNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void>handlePasswordNotValidException(PasswordNotValidException e) {
        log.error("PasswordNotValidExeption Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(UserIdNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void>handleUserIdNotValidException(UserIdNotValidException e) {
        log.error("UserIdNotValidException Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(NicknameNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void>handleNicknameNotValidException(NicknameNotValidException e) {
        log.error("NicknameNotValidExeption Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(LanguageNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void>handleLanguageNotValidException(LanguageNotValidException e) {
        log.error("LanguageNotValidExeption Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(NotAllowedNicknameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void>handleNotAllowedNicknameException(NotAllowedNicknameException e) {
        log.error("NotAllowedNicknameException Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(NotAllowedUseridException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void>handleNotAllowedUseridException(NotAllowedUseridException e) {
        log.error("NotAllowedUseridException Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(ForbiddenWordContainedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public CommonResponse<Void>handleForbiddenWordContainedException(ForbiddenWordContainedException e) {
        log.error("ForbiddenWordContainedException Error", e);
        return CommonResponse.conflict(e.getErrorCode());
    }
}
