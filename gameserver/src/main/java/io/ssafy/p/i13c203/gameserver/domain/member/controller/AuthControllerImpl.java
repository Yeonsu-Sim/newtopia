package io.ssafy.p.i13c203.gameserver.domain.member.controller;

import io.ssafy.p.i13c203.gameserver.auth.security.CustomUserDetails;
import io.ssafy.p.i13c203.gameserver.domain.member.dto.response.LoginResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import io.ssafy.p.i13c203.gameserver.domain.member.dto.request.SignupRequestDto;
import io.ssafy.p.i13c203.gameserver.domain.member.dto.response.SignupResponseDto;
import io.ssafy.p.i13c203.gameserver.domain.member.service.MemberService;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class AuthControllerImpl implements AuthController {

    private final MemberService memberService;

    @Override
    @PostMapping("/auth/signup")
    public ResponseEntity<APIResponse<SignupResponseDto, Void>> signup(@Valid @RequestBody SignupRequestDto request) {
        SignupResponseDto response = memberService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                APIResponse.success("회원가입에 성공했습니다",response)
        );
    }

    @Override
    @GetMapping("/public/me")
    public ResponseEntity<APIResponse<LoginResponseDto, Void>> me() {
        Long memberId = null;

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null &&
          authentication.isAuthenticated() &&
          !(authentication instanceof AnonymousAuthenticationToken)) {

            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                memberId = userDetails.getMemberId();
            }
        }

        var res = memberService.me(memberId);

        return ResponseEntity.status(HttpStatus.OK).body(
            APIResponse.success(memberId== null ? "로그인 정보 없음" :"로그인 조회 완료", res)
        );
    }
}
