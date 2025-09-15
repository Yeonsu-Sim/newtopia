package io.ssafy.p.i13c203.gameserver.auth.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomLogoutHandler implements LogoutHandler {

    @Value("${cookie.access-token-name:accessToken}")
    private String accessTokenCookieName;

    @Value("${cookie.refresh-token-name:refreshToken}")
    private String refreshTokenCookieName;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info("로그아웃 처리 시작");

        // JWT 토큰 쿠키 삭제
        clearCookie(response, accessTokenCookieName);
        clearCookie(response, refreshTokenCookieName);

        // 기존 호환성을 위한 쿠키 삭제
        clearCookie(response, "memberId");
        clearCookie(response, "email");
        clearCookie(response, "nickname");

        log.info("로그아웃 완료");
    }

    private void clearCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}