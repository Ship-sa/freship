package me.yeon.freship.common.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.yeon.freship.common.domain.constant.ErrorCode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {
    private String name;
    private String code;
    private String message;

    private ErrorResponse(String name, String code, String message) {
        this.name = name;
        this.code = code;
        this.message = message;
    }

    public static ErrorResponse of(ErrorCode code) {
        return new ErrorResponse(code.name(), code.getCode(), code.getMessage());
    }
}
