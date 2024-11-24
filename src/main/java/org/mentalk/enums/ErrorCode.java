package org.mentalk.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {
    // 400 Bad Request
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "B001", "입력한 이메일이 존재하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "B002", "입력한 비밀번호가 일치하지 않습니다."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "U001", "인증에 실패했습니다."),

    // 403 Forbidden
    FORBIDDEN(HttpStatus.FORBIDDEN, "F001", "접근 권한이 없습니다."),

    // 409 Conflict
    EMAIL_DUPLICATE(HttpStatus.CONFLICT, "C001", "이미 사용 중인 이메일입니다."),
    PHONE_NUMBER_DUPLICATE(HttpStatus.CONFLICT, "C002", "이미 존재하는 회원입니다."),

    // 500 Server Error
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "예기치 않은 오류가 발생했습니다."),
    DATA_INTEGRITY_VIOLATION(HttpStatus.INTERNAL_SERVER_ERROR, "S002", "데이터 무결성 위반 문제가 발생했습니다."),
    JWT_CREATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S003", "JWT 생성 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
