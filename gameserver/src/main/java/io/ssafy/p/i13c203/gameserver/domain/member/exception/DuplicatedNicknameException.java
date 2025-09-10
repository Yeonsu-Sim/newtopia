package io.ssafy.p.i13c203.gameserver.domain.member.exception;

import io.ssafy.p.i13c203.gameserver.global.exception.DuplicatedException;
import io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode;

public class DuplicatedNicknameException extends DuplicatedException {

    public DuplicatedNicknameException() {
        super(ErrorCode.DUPLICATED_NICKNAME);
    }
    
    public DuplicatedNicknameException(String message) {
        super(ErrorCode.DUPLICATED_NICKNAME, message);
    }
}
