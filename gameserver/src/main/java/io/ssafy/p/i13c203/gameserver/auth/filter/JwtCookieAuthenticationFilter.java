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
        String requestURI = request.getRequestURI();
        log.trace("JWT Token 인증 과정 시작 - 요청 경로: {}", requestURI);

        String accessToken = getTokenFromCookie(request, accessTokenCookieName);
        log.trace("AccessToken 쿠키에서 추출 완료 - 토큰 존재: {}", accessToken != null ? "예" : "아니오");

        if (StringUtils.hasText(accessToken)) {
            log.trace("AccessToken 검증 시작");
            boolean isValid = jwtUtil.validateToken(accessToken);
            log.trace("AccessToken 검증 결과: {}", isValid ? "유효" : "무효");

            if (isValid) {
                log.trace("유효한 AccessToken으로 인증 설정 시작");
                setAuthentication(accessToken, request);
                log.trace("AccessToken 인증 처리 완료");
                filterChain.doFilter(request, response);
                return;
            }
        } else {
            log.trace("AccessToken이 없어서 RefreshToken 확인");
        }

        // Access Token이 만료되었거나 없을 때, Refresh Token 확인
        log.trace("RefreshToken 확인 시작");
        String refreshToken = getTokenFromCookie(request, refreshTokenCookieName);
        log.trace("RefreshToken 쿠키에서 추출 완료 - 토큰 존재: {}", refreshToken != null ? "예" : "아니오");

        if (StringUtils.hasText(refreshToken)) {
            log.trace("RefreshToken 검증 시작");
            boolean isRefreshValid = jwtUtil.validateToken(refreshToken);
            log.trace("RefreshToken 검증 결과: {}", isRefreshValid ? "유효" : "무효");

            if (isRefreshValid) {
                // 새로운 Access Token 생성 및 쿠키 설정
                log.trace("RefreshToken으로 새 AccessToken 생성 시작");
                Long memberId = jwtUtil.getMemberIdFromToken(refreshToken);
                log.trace("RefreshToken에서 회원 ID 추출: {}", memberId);

                String newAccessToken = jwtUtil.generateAccessToken(memberId);
                log.trace("새 AccessToken 생성 완료");

                setAuthenticationCookie(response, accessTokenCookieName, newAccessToken, 15 * 60); // 15분
                log.trace("새 AccessToken 쿠키 설정 완료");

                setAuthentication(newAccessToken, request);
                log.debug("Access token refreshed for member: {}", memberId);
                log.trace("RefreshToken 인증 처리 완료");
            } else {
                log.trace("RefreshToken이 무효하여 인증 실패");
            }
        } else {
            log.trace("RefreshToken도 없어서 인증 불가");
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
            log.trace("인증 객체 설정 시작");
            Long memberId = jwtUtil.getMemberIdFromToken(token);
            log.trace("토큰에서 회원 ID 추출 성공: {}", memberId);

            UserDetails userDetails = userDetailsService.loadUserByMemberId(memberId);
            log.trace("회원 정보 조회 성공 - 회원 ID: {}, 이메일: {}", memberId, userDetails.getUsername());

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                    null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            log.trace("인증 토큰 객체 생성 완료");

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.trace("SecurityContext에 인증 정보 설정 완료 - 인증 성공");
        } catch (Exception e) {
            log.trace("JWT 인증 실패 - 예외 발생: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            log.debug("JWT authentication failed: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            log.trace("SecurityContext 초기화 완료");
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