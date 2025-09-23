package io.ssafy.p.i13c203.gameserver.domain.admin.controller;

import io.ssafy.p.i13c203.gameserver.auth.security.CustomUserDetails;
import io.ssafy.p.i13c203.gameserver.domain.member.entity.Role;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/admin")
public class AdminController {

    /**
     * 관리자 권한 확인 엔드포인트
     * JWT 토큰이 있고 ADMIN 역할인 경우에만 성공
     */
    @GetMapping("/verify")
    public ResponseEntity<APIResponse<Map<String, Object>, Void>> verifyAdminAuth(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                APIResponse.fail("UNAUTHORIZED", "로그인이 필요합니다.")
            );
        }

        // 관리자 권한 확인
        if (!userDetails.getMember().getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                APIResponse.fail("FORBIDDEN", "관리자 권한이 필요합니다.")
            );
        }

        // 관리자 정보 반환
        Map<String, Object> adminInfo = new HashMap<>();
        adminInfo.put("id", userDetails.getMemberId());
        adminInfo.put("email", userDetails.getMember().getEmail());
        adminInfo.put("nickname", userDetails.getMember().getNickname());
        adminInfo.put("role", userDetails.getMember().getRole().name());

        return ResponseEntity.ok(
            APIResponse.success("관리자 권한 확인 완료", adminInfo)
        );
    }
}