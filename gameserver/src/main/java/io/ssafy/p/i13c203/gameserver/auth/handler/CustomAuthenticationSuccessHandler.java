package io.ssafy.p.i13c203.gameserver.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ssafy.p.i13c203.gameserver.auth.jwt.JwtUtil;
import io.ssafy.p.i13c203.gameserver.auth.security.CustomUserDetails;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Value("${cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${cookie.same-site:Lax}")
    private String cookieSameSite;
    
    @Value("${cookie.access-token-name:accessToken}")
    private String accessTokenCookieName;

    @Value("${cookie.refresh-token-name:refreshToken}")
    private String refreshTokenCookieName;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = userDetails.getMemberId();

        // JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(memberId);
        String refreshToken = jwtUtil.generateRefreshToken(memberId);

        // 쿠키에 토큰 저장
        setAuthenticationCookie(response, accessTokenCookieName, accessToken, 15 * 60); // 15분
        setAuthenticationCookie(response, refreshTokenCookieName, refreshToken, 7 * 24 * 60 * 60); // 7일

        // JSON 응답 생성
        Map<String, Object> responseData = new HashMap<>();

        APIResponse<Map<String, Object>, Void> apiResponse =
            APIResponse.success("로그인에 성공했습니다", responseData);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

        log.info("로그인 성공: memberId={}, email={}", memberId, userDetails.getUsername());
    }

    private void setAuthenticationCookie(HttpServletResponse response, String name, String value, int maxAgeSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeSeconds);
        cookie.setSecure(cookieSecure);

        response.addCookie(cookie);

        // SameSite 속성 추가
        if (cookieSameSite != null && !cookieSameSite.isEmpty()) {
            String cookieHeader = String.format("%s=%s; Path=/; Max-Age=%d; HttpOnly%s; SameSite=%s",
                    name, value, maxAgeSeconds,
                    cookieSecure ? "; Secure" : "",
                    cookieSameSite);
            response.addHeader("Set-Cookie", cookieHeader);
        }
    }
}