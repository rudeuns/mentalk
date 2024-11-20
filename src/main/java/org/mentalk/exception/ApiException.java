package org.mentalk.exception;

import lombok.Getter;
import org.mentalk.enums.ErrorCode;

@Getter
public class ApiException extends RuntimeException {
    private final ErrorCode errorCode;

    public ApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
