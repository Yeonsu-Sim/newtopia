package io.ssafy.p.i13c203.gameserver.global.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException{

    private ErrorCode errorCode;

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode.defaultMessage());
        this.errorCode = errorCode;
    }

    public NotFoundException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
