package me.yeon.freship.common.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.yeon.freship.common.domain.constant.ErrorCode;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientException extends RuntimeException {
    private ErrorCode errorCode;

    public ClientException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
