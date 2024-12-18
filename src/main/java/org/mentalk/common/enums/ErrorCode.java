package org.mentalk.common.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {
    // Global Exception
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G001", "예기치 않은 오류가 발생했습니다."),

    DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "G002", "데이터 무결성 위반 문제가 발생했습니다."),
    METHOD_ARG_NOT_VALID(HttpStatus.BAD_REQUEST, "G003", "요청 데이터가 유효하지 않습니다."),
    HTTP_MESSAGE_NOT_READABLE(HttpStatus.BAD_REQUEST, "G004", "요청 본문이 잘못된 형식입니다."),
    HTTP_MESSAGE_CONVERSION(HttpStatus.BAD_REQUEST, "G005", "요청 본문의 값 중 일부가 변환에 실패했습니다."),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "G006", "인증에 실패했습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "G007", "접근 권한이 없습니다."),

    // 401 UnAuthorized
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "A001", "비밀번호가 일치하지 않습니다."),

    // 404 Not Found
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "N001", "가입되지 않은 이메일입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "N002", "회원 정보를 찾을 수 없습니다."),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "N003", "계정 정보를 찾을 수 없습니다."),

    // 409 Conflict
    ALREADY_EMAIL_IN_USE(HttpStatus.CONFLICT, "C001", "이미 사용 중인 이메일입니다."),
    ALREADY_ACCOUNT_REGISTERED(HttpStatus.CONFLICT, "C002", "이미 계정이 등록된 회원입니다."),

    // 500 Server Error
    JWT_CREATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "JWT 생성 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
