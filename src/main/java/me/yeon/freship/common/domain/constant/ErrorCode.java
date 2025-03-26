package me.yeon.freship.common.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Member 에러코드
    INVALID_MEMBER_ROLE(HttpStatus.BAD_REQUEST, "MEMBER-1", "유효하지 않은 권한입니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "MEMBER-2", "이미 존재하는 이메일입니다."),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "MEMBER-3", "존재하지 않는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER-4", "잘못된 비밀번호입니다."),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "MEMBER-5", "유효하지 않는 JWT 서명입니다."),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "MEMBER-6", "만료된 JWT 토큰입니다."),
    UNSUPPORTED_JWT(HttpStatus.BAD_REQUEST, "MEMBER-7", "지원되지 않는 JWT 토큰입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER-8", "내부 서버 오류로 인해 요청을 처리할 수 없습니다."),
    NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND, "MEMBER-9", "존재하지 않는 토큰입니다."),
    SAME_AS_OLD_PASSWORD(HttpStatus.NOT_FOUND, "MEMBER-10", "새 비밀번호는 기존 비밀번호와 같을 수 없습니다."),

    EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "EXCEPTION", "알 수 없는 에러입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
