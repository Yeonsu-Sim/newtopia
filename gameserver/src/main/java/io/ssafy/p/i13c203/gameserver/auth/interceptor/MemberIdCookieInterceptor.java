package io.ssafy.p.i13c203.gameserver.auth.interceptor;

import io.ssafy.p.i13c203.gameserver.auth.CurrentMemberContext;
import io.ssafy.p.i13c203.gameserver.global.exception.BusinessException;
import io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * 로그인 시 심은 HttpOnly Cookie("memberId")를 읽어 컨텍스트에 주입하는 인터셉터.
 */
@Component
public class MemberIdCookieInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            // Preflight는 컨텍스트 주입/로그 생략
            return true;
        }
        if (!(handler instanceof org.springframework.web.method.HandlerMethod)) {
            return true; // 정적 리소스 등도 패스
        }
        Long id = extractMemberId(request.getCookies());
        CurrentMemberContext.set(id);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        CurrentMemberContext.clear();
    }

    private Long extractMemberId(@Nullable Cookie[] cookies) {
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if ("memberId".equals(c.getName())) {
                try {
                    return Long.parseLong(c.getValue());
                } catch (Exception e) {
                    throw new BusinessException(ErrorCode.INVALID_MEMBER_ID_COOKIE);
                }
            }
        }
        return null;
    }
}