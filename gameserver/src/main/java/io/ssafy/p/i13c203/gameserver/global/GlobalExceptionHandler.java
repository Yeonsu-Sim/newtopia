package io.ssafy.p.i13c203.gameserver.global;

import io.ssafy.p.i13c203.gameserver.domain.member.exception.DuplicatedEmailException;
import io.ssafy.p.i13c203.gameserver.domain.member.exception.DuplicatedNicknameException;
import io.ssafy.p.i13c203.gameserver.domain.member.exception.InvalidPasswordException;
import io.ssafy.p.i13c203.gameserver.domain.member.exception.MemberNotFoundException;
import io.ssafy.p.i13c203.gameserver.global.exception.DuplicatedException;
import io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode;
import io.ssafy.p.i13c203.gameserver.global.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicatedException.class)
    public ResponseEntity<APIResponse<?,?>> handleDuplicatedException(DuplicatedException e){
        return ResponseEntity.badRequest()
                .body( APIResponse.fail(e.getErrorCode().code(), e.getMessage()));
    }


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<APIResponse<?,?>> handleMemberNotFound(NotFoundException e){
        return ResponseEntity.badRequest()
                .body(APIResponse.fail(e.getErrorCode().code(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<?,?>> handleNotValidException(MethodArgumentNotValidException e){
        return ResponseEntity.badRequest()
                .body(APIResponse.fail(ErrorCode.NOT_VALID.code(), e.getBindingResult().getFieldError().getDefaultMessage()));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<APIResponse<?,?>> handleInvalidPasswordException(InvalidPasswordException e){
        return ResponseEntity.badRequest()
                .body(APIResponse.fail(e.getErrorCode().code(), e.getMessage()));
    }


}
