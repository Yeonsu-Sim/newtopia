package io.ssafy.p.i13c203.gameserver.domain.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.ssafy.p.i13c203.gameserver.domain.member.dto.request.LoginRequestDto;
import io.ssafy.p.i13c203.gameserver.domain.member.dto.request.SignupRequestDto;
import io.ssafy.p.i13c203.gameserver.domain.member.dto.response.LoginResponseDto;
import io.ssafy.p.i13c203.gameserver.domain.member.dto.response.SignupResponseDto;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "인증 API", description = "회원 인증 관련 API (실제 처리는 Spring Security Filter에서 수행)")
public interface AuthController {

    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "회원가입 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일 또는 닉네임")
    })
    ResponseEntity<APIResponse<SignupResponseDto, Void>> signup(
            @Parameter(description = "회원가입 요청 정보")
            @RequestBody @Valid SignupRequestDto request
    );

    @Operation(
      summary = "로그인 확인",
      description = "로그인 여부와 상관없이 언제나 200을 보냅니다. 단, 로그인이 안될 경우 내부 요소가 null로 표현됩니다."
    )
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "로그인 여부 조회 성공(로그인 여부와 상관없이 출력)"),
    })
    ResponseEntity<APIResponse<LoginResponseDto, Void>> me();

    @Operation(
        summary = "로그인",
        description = "이메일과 비밀번호로 로그인합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공 - JWT 토큰이 쿠키로 설정됩니다"),
        @ApiResponse(responseCode = "401", description = "인증 실패 - 이메일 또는 비밀번호 불일치")
    })
    @PostMapping("/auth/login")
    default ResponseEntity<APIResponse<LoginResponseDto, Void>> login(
            @Parameter(description = "로그인 요청 정보")
            @RequestBody @Valid LoginRequestDto request
    ){
        throw new UnsupportedOperationException("This method is for documentation only. Actual login is handled by Spring Security.");
    }

    @Operation(
        summary = "로그아웃",
        description = "현재 세션을 종료하고 JWT 쿠키를 삭제합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그아웃 성공 - 모든 인증 쿠키가 삭제됩니다")
    })
    @PostMapping("/auth/logout")
    default ResponseEntity<APIResponse<Void, Void>> logout(){
        throw new UnsupportedOperationException("This method is for documentation only. Actual logout is handled by Spring Security.");
    }
}