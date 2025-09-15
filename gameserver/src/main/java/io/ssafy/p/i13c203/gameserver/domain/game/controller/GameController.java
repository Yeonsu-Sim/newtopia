package io.ssafy.p.i13c203.gameserver.domain.game.controller;

import io.ssafy.p.i13c203.gameserver.auth.security.CustomUserDetails;
import io.ssafy.p.i13c203.gameserver.domain.game.dto.request.CreateGameRequest;
import io.ssafy.p.i13c203.gameserver.domain.game.dto.request.SubmitChoiceRequest;
import io.ssafy.p.i13c203.gameserver.domain.game.dto.response.*;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "게임 API", description = "게임 생성, 진행, 선택지 제출 관련 API")
public interface GameController {

    @Operation(summary = "내 진행중인 게임 조회", description = "현재 로그인된 사용자의 진행 중인 게임 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "게임 정보 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "404", description = "진행 중인 게임이 없음")
    })
    @GetMapping("/me")
    ResponseEntity<APIResponse<GetMyGameResponse, Void>> getMyGame(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(summary = "새 게임 생성", description = "새로운 게임을 생성합니다. force=true 시 기존 게임을 종료하고 새 게임을 시작합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "게임 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "409", description = "이미 진행중인 게임이 있음 (force=false인 경우)")
    })
    @PostMapping
    ResponseEntity<APIResponse<GameDetailResponse, Void>> createGame(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "기존 게임 강제 종료 여부", example = "false")
            @RequestParam(name = "force", defaultValue = "false") boolean force,
            @Parameter(description = "게임 생성 요청 정보")
            @RequestBody @Valid CreateGameRequest request
    );

    @Operation(summary = "게임 상세 조회", description = "특정 게임의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "게임 상세 정보 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "해당 게임에 접근 권한 없음"),
        @ApiResponse(responseCode = "404", description = "게임을 찾을 수 없음")
    })
    @GetMapping("/{gameId}")
    ResponseEntity<APIResponse<GameDetailResponse, Void>> getGame(
            @Parameter(description = "게임 ID", example = "1")
            @PathVariable Long gameId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(summary = "선택지 제출", description = "시나리오 카드의 선택지를 선택하여 제출합니다. Idempotency-Key 헤더로 중복 방지 가능합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "선택지 제출 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 선택지 또는 게임 상태"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "해당 게임에 접근 권한 없음"),
        @ApiResponse(responseCode = "404", description = "게임 또는 카드를 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "이미 처리된 요청 (Idempotency)")
    })
    @PostMapping("/{gameId}/choice")
    ResponseEntity<APIResponse<SubmitChoiceResponse, Void>> submitChoice(
            @Parameter(description = "게임 ID", example = "1")
            @PathVariable Long gameId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(
                    name = "Idempotency-Key",
                    in = ParameterIn.HEADER,
                    required = false, // 강제하려면 true
                    description = "동일 요청 재전송 시 멱등 보장을 위한 키"
            ) @RequestHeader String idemKey,
            @Parameter(description = "선택지 제출 요청 정보")
            @RequestBody @Valid SubmitChoiceRequest request
    );
}