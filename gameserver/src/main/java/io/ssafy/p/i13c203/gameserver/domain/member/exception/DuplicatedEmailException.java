package io.ssafy.p.i13c203.gameserver.domain.member.exception;

import io.ssafy.p.i13c203.gameserver.global.exception.DuplicatedException;
import io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode;

public class DuplicatedEmailException extends DuplicatedException {

    public DuplicatedEmailException() {
        super(ErrorCode.DUPLICATED_EMAIL);
    }

    public DuplicatedEmailException(String message) {
        super(ErrorCode.DUPLICATED_EMAIL, message);
    }

}
