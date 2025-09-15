package io.ssafy.p.i13c203.gameserver.auth;

import io.micrometer.common.lang.Nullable;
import io.ssafy.p.i13c203.gameserver.global.exception.BusinessException;
import io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode;

/**
 * 요청별 현재 회원 정보를 보관하는 컨텍스트 (Spring Security 미사용)
 */
public class CurrentMemberContext {

    private static final ThreadLocal<Long> HOLDER = new ThreadLocal<>();

    public static void set(@Nullable Long id) {
        HOLDER.set(id);
    }

    public static @Nullable Long get() {
        return HOLDER.get();
    }

    public static Long getRequired() {
        Long id = HOLDER.get();
        if (id == null) throw new BusinessException(ErrorCode.AUTH_REQUIRED);
        return id;
    }

    public static void clear() {
        HOLDER.remove();
    }
}