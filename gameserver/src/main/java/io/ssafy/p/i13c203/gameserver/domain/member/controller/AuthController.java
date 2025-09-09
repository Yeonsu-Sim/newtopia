package io.ssafy.p.i13c203.gameserver.domain.member.controller;

import io.ssafy.p.i13c203.gameserver.domain.member.dto.request.LoginRequestDto;
import io.ssafy.p.i13c203.gameserver.domain.member.dto.request.SignupRequestDto;
import io.ssafy.p.i13c203.gameserver.domain.member.dto.response.LoginResponseDto;
import io.ssafy.p.i13c203.gameserver.domain.member.dto.response.SignupResponseDto;
import io.ssafy.p.i13c203.gameserver.domain.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@Valid @RequestBody SignupRequestDto request) {
        SignupResponseDto response = memberService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request, 
                                                  HttpServletResponse response) {
        LoginResponseDto loginResponse = memberService.login(request);
        
        // 쿠키에 회원 정보 저장
        Cookie memberIdCookie = new Cookie("memberId", String.valueOf(loginResponse.getId()));
        memberIdCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        memberIdCookie.setPath("/");
        memberIdCookie.setHttpOnly(true);
        response.addCookie(memberIdCookie);
        
        Cookie emailCookie = new Cookie("email", loginResponse.getEmail());
        emailCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        emailCookie.setPath("/");
        emailCookie.setHttpOnly(true);
        response.addCookie(emailCookie);
        
        Cookie nicknameCookie = new Cookie("nickname", loginResponse.getNickname());
        nicknameCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        nicknameCookie.setPath("/");
        nicknameCookie.setHttpOnly(true);
        response.addCookie(nicknameCookie);
        
        return ResponseEntity.ok(loginResponse);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // 쿠키 삭제 (maxAge를 0으로 설정)
        Cookie memberIdCookie = new Cookie("memberId", null);
        memberIdCookie.setMaxAge(0);
        memberIdCookie.setPath("/");
        response.addCookie(memberIdCookie);
        
        Cookie emailCookie = new Cookie("email", null);
        emailCookie.setMaxAge(0);
        emailCookie.setPath("/");
        response.addCookie(emailCookie);
        
        Cookie nicknameCookie = new Cookie("nickname", null);
        nicknameCookie.setMaxAge(0);
        nicknameCookie.setPath("/");
        response.addCookie(nicknameCookie);
        
        log.info("로그아웃 완료");
        
        return ResponseEntity.ok().build();
    }
}
