package org.mentalk.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.mentalk.common.dto.ApiResponse;
import org.mentalk.common.enums.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse> handleApiException(ApiException e) {
        ErrorCode errorCode = e.getErrorCode();

        log.error("{} {}", errorCode.getCode(), errorCode.getMessage(), e);

        return ResponseEntity.status(errorCode.getStatus())
                             .body(ApiResponse.failure(errorCode.getCode(),
                                                       errorCode.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleExceptions(Exception e) {
        ErrorCode errorCode = switch (e.getClass().getSimpleName()) {
            case "DataIntegrityViolationException" -> ErrorCode.DATA_INTEGRITY_VIOLATION;
            case "MethodArgumentNotValidException" -> ErrorCode.METHOD_ARG_NOT_VALID;
            case "HttpMessageNotReadableException" -> ErrorCode.HTTP_MESSAGE_NOT_READABLE;
            case "HttpMessageConversionException" -> ErrorCode.HTTP_MESSAGE_CONVERSION;
            default -> ErrorCode.UNEXPECTED_ERROR;
        };

        log.error("{} {}", errorCode.getCode(), errorCode.getMessage(), e);

        return ResponseEntity.status(errorCode.getStatus())
                             .body(ApiResponse.failure(errorCode.getCode(),
                                                       errorCode.getMessage()));
    }
}
