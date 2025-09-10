package io.ssafy.p.i13c203.gameserver.domain.member.exception;

import io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidPasswordException extends RuntimeException{
    
    private ErrorCode errorCode;

    public InvalidPasswordException(){
        super(ErrorCode.INVALID_PASSWORD.defaultMessage());
        this.errorCode = ErrorCode.INVALID_PASSWORD;
    }
    
    public InvalidPasswordException(String message){
        super(message);
        this.errorCode = ErrorCode.INVALID_PASSWORD;
    }
}
