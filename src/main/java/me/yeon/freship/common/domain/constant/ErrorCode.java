package me.yeon.freship.common.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Member 에러코드
    INVALID_MEMBER_ROLE(HttpStatus.BAD_REQUEST, "MEMBER-1", "유효하지 않은 권한입니다."),

    EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "EXCEPTION", "알 수 없는 에러입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
