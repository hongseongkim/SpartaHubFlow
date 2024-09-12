package com.sparta.hub.application.exception;

import jakarta.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.Objects;
import javax.naming.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private void logError(Exception e) {
        log.error("{} : {}", e.getClass().getSimpleName(), e.getMessage());
    }

    // 1. 입력 값 검증 관련 예외

    /**
     * 요청 객체의 필드 유효성 검사 실패 시 발생하는 예외를 처리합니다.
     * 주로 @Valid 어노테이션을 사용한 검증에서 발생합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("MethodArgumentNotValidException : {}", errorMessage);
        return buildErrorResponse(ErrorCode.INVALID_INPUT_VALUE, errorMessage);
    }

    /**
     * 요청 파라미터의 유효성 검사 실패 시 발생하는 예외를 처리합니다.
     * 주로 @Validated 어노테이션을 사용한 컨트롤러 메서드 파라미터 검증에서 발생합니다.
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        String errorMessage = (String) Objects.requireNonNull(e.getDetailMessageArguments())[0];
        logError(e);
        return buildErrorResponse(ErrorCode.MISSING_INPUT_VALUE, errorMessage);
    }

    /**
     * 필수 요청 값이 누락되었을 때 발생하는 예외를 처리합니다.
     * 주로 @RequestParam, @PathVariable 등의 필수 파라미터가 누락된 경우 발생합니다.
     */
    @ExceptionHandler(MissingRequestValueException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestValueException(MissingRequestValueException e) {
        logError(e);
        return buildErrorResponse(ErrorCode.MISSING_INPUT_VALUE);
    }

    /**
     * 요청 파라미터의 타입이 예상과 다를 때 발생하는 예외를 처리합니다.
     * 예를 들어, 정수형 파라미터에 문자열이 입력된 경우 발생합니다.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        logError(e);
        return buildErrorResponse(ErrorCode.INVALID_INPUT_VALUE);
    }

    /**
     * 비즈니스 로직에서 유효하지 않은 인자가 사용되었을 때 발생하는 예외를 처리합니다.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        logError(e);
        return buildErrorResponse(ErrorCode.INVALID_INPUT_VALUE);
    }

    // 2. HTTP 요청 관련 예외

    /**
     * JSON 요청 본문을 파싱할 수 없을 때 발생하는 예외를 처리합니다.
     * 주로 잘못된 형식의 JSON이 전송되었을 때 발생합니다.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logError(e);
        return buildErrorResponse(ErrorCode.INVALID_JSON);
    }

    /**
     * 지원하지 않는 HTTP 메소드로 요청이 왔을 때 발생하는 예외를 처리합니다.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        logError(e);
        return buildErrorResponse(ErrorCode.METHOD_NOT_ALLOWED);
    }

    /**
     * 요청한 URI에 해당하는 핸들러를 찾을 수 없을 때 발생하는 예외를 처리합니다.
     * 주로 존재하지 않는 API 엔드포인트로 요청이 왔을 때 발생합니다.
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException e) {
        logError(e);
        return buildErrorResponse(ErrorCode.NOT_EXIST_API);
    }

    // 3. 비즈니스 로직 관련 예외

    /**
     * 요청한 엔티티를 찾을 수 없을 때 발생하는 예외를 처리합니다.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        logError(e);
        return buildErrorResponse(ErrorCode.ENTITY_NOT_FOUND, e.getMessage());
    }

    /**
     * 사용자 정의 서비스 예외를 처리합니다.
     * 비즈니스 로직에서 발생하는 예외들을 처리합니다.
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException e) {
        logError(e);
        return buildErrorResponse(e.getErrorCode());
    }

    // 4. 보안 관련 예외

    /**
     * 인증 실패 시 발생하는 예외를 처리합니다.
     * 주로 로그인 실패나 유효하지 않은 토큰 사용 시 발생합니다.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        logError(e);
        return buildErrorResponse(ErrorCode.UNAUTHORIZED, e.getMessage());
    }

    /**
     * 접근 권한이 없을 때 발생하는 예외를 처리합니다.
     * 주로 인증된 사용자가 특정 리소스에 접근할 권한이 없을 때 발생합니다.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        logError(e);
        return buildErrorResponse(ErrorCode.FORBIDDEN, e.getMessage());
    }

    // 5. 가장 일반적인 예외

    /**
     * 위에서 처리되지 않은 모든 예외를 처리합니다.
     * 예상치 못한 서버 오류 등이 여기서 처리됩니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        logError(e);
        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(new ErrorResponse(errorCode));
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(new ErrorResponse(errorCode, message));
    }
}
