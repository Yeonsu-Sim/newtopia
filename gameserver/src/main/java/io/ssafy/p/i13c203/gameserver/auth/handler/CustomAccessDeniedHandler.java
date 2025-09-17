package io.ssafy.p.i13c203.gameserver.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        log.error("=== 접근 권한 거부 발생 ===");
        log.error("요청 URI: {}", request.getRequestURI());
        log.error("HTTP 메서드: {}", request.getMethod());
        log.error("예외 메시지: {}", accessDeniedException.getMessage());

        // SecurityContext에서 현재 인증 정보 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            log.error("현재 인증 상태:");
            log.error("  - Principal: {}", authentication.getPrincipal());
            log.error("  - Authorities: {}", authentication.getAuthorities());
            log.error("  - Authenticated: {}", authentication.isAuthenticated());
            log.error("  - Details: {}", authentication.getDetails());
        } else {
            log.error("SecurityContext에 인증 정보 없음");
        }

        // 요청 헤더 정보
        log.error("요청 헤더:");
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            log.error("  - {}: {}", headerName, request.getHeader(headerName));
        });

        // 스택 트레이스 로그
        log.error("AccessDeniedException 스택 트레이스:", accessDeniedException);

        // 응답 생성
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        APIResponse<?, ?> apiResponse = APIResponse.fail("ACCESS_DENIED",
            "접근 권한이 없습니다. URI: " + request.getRequestURI());

        String jsonResponse = objectMapper.writeValueAsString(apiResponse);
        response.getWriter().write(jsonResponse);

        log.error("403 응답 전송 완료");
    }
}