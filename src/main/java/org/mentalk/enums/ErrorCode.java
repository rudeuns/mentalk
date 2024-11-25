package org.mentalk.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {
    // 400 Bad Request
    DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "B001", "데이터 무결성 위반 문제가 발생했습니다."),
    METHOD_ARG_NOT_VALID(HttpStatus.BAD_REQUEST, "B002", "요청 데이터가 유효하지 않습니다."),
    HTTP_MESSAGE_NOT_READABLE(HttpStatus.BAD_REQUEST, "B003", "요청 본문이 잘못된 JSON 형식입니다."),
    HTTP_MESSAGE_CONVERSION(HttpStatus.BAD_REQUEST, "B004", "요청 본문의 값 중 일부가 변환에 실패했습니다."),

    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "B101", "입력한 이메일이 존재하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "B102", "입력한 비밀번호가 일치하지 않습니다."),

    // 401 Unauthorized
    SC_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "U001", "[SecurityConfig] 인증에 실패했습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "U002", "인증에 실패했습니다."),

    // 403 Forbidden
    SC_FORBIDDEN(HttpStatus.FORBIDDEN, "F001", "[SecurityConfig] 접근 권한이 없습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "F002", "접근 권한이 없습니다."),

    // 404 Not Found
    MENTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "N101", "멘토 회원이 존재하지 않습니다."),

    // 409 Conflict
    EMAIL_DUPLICATE(HttpStatus.CONFLICT, "C101", "이미 사용 중인 이메일입니다."),
    PHONE_NUMBER_DUPLICATE(HttpStatus.CONFLICT, "C102", "이미 존재하는 회원입니다."),

    // 500 Server Error
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "예기치 않은 오류가 발생했습니다."),

    JWT_CREATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S101", "JWT 생성 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
