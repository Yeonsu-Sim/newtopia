package io.ssafy.p.i13c203.gameserver.domain.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/api/v1/auth")
public class AuthControllerImpl implements AuthController {

    private final MemberService memberService;

    @Override
    public ResponseEntity<APIResponse<SignupResponseDto, Void>> signup(@Valid @RequestBody SignupRequestDto request) {
        SignupResponseDto response = memberService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                APIResponse.success("회원가입에 성공했습니다",response)
        );
    }
}
