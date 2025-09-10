package io.ssafy.p.i13c203.gameserver.global.exception;

import lombok.Getter;

@Getter
public class DuplicatedException extends RuntimeException{

    ErrorCode errorCode;

    public DuplicatedException(ErrorCode errorCode) {
        super(errorCode.defaultMessage());
        this.errorCode = errorCode;
    }
    public DuplicatedException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }


}


