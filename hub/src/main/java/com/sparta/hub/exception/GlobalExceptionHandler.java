package com.sparta.hub.exception;

import com.sparta.hub.application.common.response.ResponseBody;
import com.sparta.hub.application.common.response.ResponseUtil;
import java.util.Objects;
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

    @ExceptionHandler(MethodArgumentNotValidException.class) // Validation 에러
    public ResponseEntity<ResponseBody<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("MethodArgumentNotValidException : {}", errorMessage);
        return buildErrorResponse(ErrorCode.INVALID_INPUT_VALUE, errorMessage);
    }

    @ExceptionHandler(HandlerMethodValidationException.class) // RequestParam validation 실패
    public ResponseEntity<ResponseBody<Void>> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        String errorMessage = (String) Objects.requireNonNull(e.getDetailMessageArguments())[0];
        logError(e);
        return buildErrorResponse(ErrorCode.MISSING_INPUT_VALUE, errorMessage);
    }

    @ExceptionHandler(MissingRequestValueException.class) // 요청 데이터 부족 (필수 값 누락)
    public ResponseEntity<ResponseBody<Void>> handleMissingRequestValueException(MissingRequestValueException e) {
        logError(e);
        return buildErrorResponse(ErrorCode.MISSING_INPUT_VALUE);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class) // 쿼리 파라미터 타입 매칭 실패
    public ResponseEntity<ResponseBody<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        logError(e);
        return buildErrorResponse(ErrorCode.INVALID_INPUT_VALUE);
    }

    @ExceptionHandler(IllegalArgumentException.class) // Validation 실패 (잘못된 값)
    public ResponseEntity<ResponseBody<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        logError(e);
        return buildErrorResponse(ErrorCode.INVALID_INPUT_VALUE);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class) // JSON 파싱 오류
    public ResponseEntity<ResponseBody<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logError(e);
        return buildErrorResponse(ErrorCode.INVALID_JSON);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class) // 잘못된 HttpMethod
    public ResponseEntity<ResponseBody<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        logError(e);
        return buildErrorResponse(ErrorCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NoHandlerFoundException.class) // 존재하지 않는 API 요청 (URI 오류)
    public ResponseEntity<ResponseBody<Void>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        logError(e);
        return buildErrorResponse(ErrorCode.NOT_EXIST_API);
    }

    @ExceptionHandler(ServiceException.class) // Custom ServiceException 처리
    public ResponseEntity<ResponseBody<Void>> handleServiceException(ServiceException e) {
        logError(e);
        return buildErrorResponse(e.getErrorCode());
    }

//    @ExceptionHandler(AccessDeniedException.class) // 권한 부족 에러
//    public ResponseEntity<ResponseBody<Void>> handleAccessDeniedException(AccessDeniedException e) {
//        logError(e);
//        return buildErrorResponse(ErrorCode.FORBIDDEN, e.getMessage());
//    }

    @ExceptionHandler(Exception.class) // 기타 예외 처리
    public ResponseEntity<ResponseBody<Void>> handleException(Exception e) {
        logError(e);
        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ResponseBody<Void>> buildErrorResponse(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(ResponseUtil.createFailureResponse(errorCode));
    }

    private ResponseEntity<ResponseBody<Void>> buildErrorResponse(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(ResponseUtil.createFailureResponse(errorCode, message));
    }
}
