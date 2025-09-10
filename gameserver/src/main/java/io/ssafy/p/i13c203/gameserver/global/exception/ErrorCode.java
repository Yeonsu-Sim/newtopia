package io.ssafy.p.i13c203.gameserver.global.exception;

public enum ErrorCode {
    DUPLICATED_EMAIL("DUPLICATED_EMAIL", "이미 사용 중인 이메일입니다."),
    DUPLICATED_NICKNAME("DUPLICATED_NICKNAME", "이미 사용 중인 닉네임입니다."),
    MEMBER_NOT_FOUND("MEMBER_NOT_FOUND", "해당 회원을 찾을 수 없습니다."),
    INVALID_PASSWORD("INVALID_PASSWORD", "비밀번호가 올바르지 않습니다."),
    NOT_VALID("NOT_VALID", "요청 값이 유효하지 않습니다.");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
    public String code() { return code; }
    public String defaultMessage() { return defaultMessage; }
}