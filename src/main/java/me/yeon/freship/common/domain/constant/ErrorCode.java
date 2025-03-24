package me.yeon.freship.common.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "EXCEPTION", "알 수 없는 에러입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
