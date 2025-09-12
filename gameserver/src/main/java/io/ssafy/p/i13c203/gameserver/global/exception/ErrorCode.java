package io.ssafy.p.i13c203.gameserver.global.exception;

public enum ErrorCode {
    DUPLICATED_EMAIL("DUPLICATED_EMAIL", "이미 사용 중인 이메일입니다."),
    DUPLICATED_NICKNAME("DUPLICATED_NICKNAME", "이미 사용 중인 닉네임입니다."),
    MEMBER_NOT_FOUND("MEMBER_NOT_FOUND", "해당 회원을 찾을 수 없습니다."),
    INVALID_PASSWORD("INVALID_PASSWORD", "비밀번호가 올바르지 않습니다."),
    NOT_VALID("NOT_VALID", "요청 값이 유효하지 않습니다."),

    // ===== 게임 도메인 =====
    GAME_ALREADY_ACTIVE("GAME_ALREADY_ACTIVE", "이미 진행 중인 게임이 있습니다."),
    GAME_NOT_FOUND("GAME_NOT_FOUND", "게임을 찾을 수 없습니다."),
    GAME_CLOSED("GAME_CLOSED", "종료된 게임입니다."),
    STALE_CARD("STALE_CARD", "카드 식별자가 일치하지 않습니다."),
    INVALID_CHOICE_CODE("INVALID_CHOICE_CODE", "유효하지 않은 선택 코드입니다."),
    SCENARIO_NOT_FOUND("SCENARIO_NOT_FOUND", "시나리오를 찾을 수 없습니다."),
    NPC_NOT_FOUND("NPC_NOT_FOUND", "NPC를 찾을 수 없습니다."),
    CONCURRENCY_CONFLICT("CONCURRENCY_CONFLICT", "동일 자원에 대한 동시 갱신 충돌이 발생했습니다."),


    // ===== 멱등성/인프라 =====
    IDEMPOTENCY_KEY_CONFLICT("IDEMPOTENCY_KEY_CONFLICT", "동일 멱등성 키가 다른 요청에 사용되었습니다."),
    IDEMPOTENCY_IN_PROGRESS("IDEMPOTENCY_IN_PROGRESS", "요청이 처리 중입니다. 잠시 후 다시 시도하세요."),
    REDIS_ERROR("REDIS_ERROR", "캐시 처리 중 오류가 발생했습니다."),

    AUTH_REQUIRED("AUTH_REQUIRED", "인증이 필요합니다."),
    INVALID_MEMBER_ID_COOKIE("INVALID_MEMBER_ID_COOKIE", "유효하지 않은 회원 ID 쿠키 입니다."),

    // ===== 엔딩 도메인 =====
    NOT_FOUND("NOT_FOUND", "해당하는 자원을 찾을 수 없습니다.");


    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
    public String code() { return code; }
    public String defaultMessage() { return defaultMessage; }
}