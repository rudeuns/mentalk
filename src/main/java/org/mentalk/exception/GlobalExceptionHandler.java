package org.mentalk.exception;

import lombok.extern.slf4j.Slf4j;
import org.mentalk.dto.response.ApiResponse;
import org.mentalk.enums.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse> handleApiException(ApiException e) {
        log.error(e.getMessage(), e);

        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                             .body(ApiResponse.failure(errorCode.getCode(),
                                                       errorCode.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneralException(Exception e) {
        log.error(e.getMessage(), e);

        ErrorCode errorCode = ErrorCode.UNEXPECTED_ERROR;
        return ResponseEntity.status(errorCode.getStatus())
                             .body(ApiResponse.failure(errorCode.getCode(),
                                                       errorCode.getMessage()));
    }
}
