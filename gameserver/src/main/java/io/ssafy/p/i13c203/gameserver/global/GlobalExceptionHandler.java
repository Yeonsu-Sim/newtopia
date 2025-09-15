package io.ssafy.p.i13c203.gameserver.global;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.ssafy.p.i13c203.gameserver.domain.member.exception.InvalidPasswordException;
import io.ssafy.p.i13c203.gameserver.global.exception.BusinessException;
import io.ssafy.p.i13c203.gameserver.global.exception.DuplicatedException;
import io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode;
import io.ssafy.p.i13c203.gameserver.global.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;

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

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<APIResponse<?,?>> handleBusinessException(BusinessException e){
        return ResponseEntity.badRequest()
                .body(APIResponse.fail(e.getErrorCode().code(), e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<APIResponse<?,?>> handleAuthenticationException(AuthenticationException e) {
        log.warn("인증 실패: {}", e.getMessage());
        return ResponseEntity.status(401)
                .body(APIResponse.fail("AUTH_FAILED", "인증이 필요합니다"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<APIResponse<?,?>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("접근 권한 없음: {}", e.getMessage());
        return ResponseEntity.status(403)
                .body(APIResponse.fail("ACCESS_DENIED", "접근 권한이 없습니다"));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<APIResponse<?,?>> handleNullPointerException(NullPointerException e) {
        log.error("NPE 발생: {}", e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(500) 
                .body(APIResponse.fail("NPE", "NULL POINT EXCEPTION!!!!!"));
    }

    // UnsupportedOperationException (Swagger 전용 엔드포인트)
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<APIResponse<?,?>> handleUnsupportedOperationException(UnsupportedOperationException e) {
        log.info("Swagger 문서용 엔드포인트 호출: {}", e.getMessage());
        return ResponseEntity.status(501)
                .body(APIResponse.fail("NOT_IMPLEMENTED", "이 엔드포인트는 문서화 목적입니다. 실제 처리는 Spring Security에서 수행됩니다."));
    }

}
