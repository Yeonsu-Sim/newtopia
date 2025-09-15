package io.ssafy.p.i13c203.gameserver.auth.filter;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.ssafy.p.i13c203.gameserver.auth.jwt.JwtUtil;
import io.ssafy.p.i13c203.gameserver.auth.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtCookieAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Value("${cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${cookie.same-site:Lax}")
    private String cookieSameSite;

    @Value("${cookie.access-token-name:accessToken}")
    private String accessTokenCookieName;

    @Value("${cookie.refresh-token-name:refreshToken}")
    private String refreshTokenCookieName;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // OPTIONS 요청은 인증 체크 생략
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        log.trace("JWT Token 인증 과정 시작");
        String accessToken = getTokenFromCookie(request, accessTokenCookieName);

        if (StringUtils.hasText(accessToken) && jwtUtil.validateToken(accessToken)) {
            setAuthentication(accessToken, request);
        } else {
            // Access Token이 만료되었거나 없을 때, Refresh Token 확인
            String refreshToken = getTokenFromCookie(request, refreshTokenCookieName);
            if (StringUtils.hasText(refreshToken) && jwtUtil.validateToken(refreshToken)) {
                // 새로운 Access Token 생성 및 쿠키 설정
                Long memberId = jwtUtil.getMemberIdFromToken(refreshToken);
                String newAccessToken = jwtUtil.generateAccessToken(memberId);

                setAuthenticationCookie(response, accessTokenCookieName, newAccessToken, 15 * 60); // 15분
                setAuthentication(newAccessToken, request);

                log.debug("Access token refreshed for member: {}", memberId);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> cookieName.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private void setAuthentication(String token, HttpServletRequest request) {
        try {
            Long memberId = jwtUtil.getMemberIdFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByMemberId(memberId);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                    null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.debug("JWT authentication failed: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }
    }

    private void setAuthenticationCookie(HttpServletResponse response, String name, String value, int maxAgeSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeSeconds);
        // TODO: 멀티 프로필화
        response.addCookie(cookie);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        // 인증이 필요 없는 경로들
        return path.startsWith("/api/v1/auth/") ||
                path.startsWith("/actuator/") ||
                path.startsWith("/swagger-") ||
                path.startsWith("/v3/api-docs");
    }
}