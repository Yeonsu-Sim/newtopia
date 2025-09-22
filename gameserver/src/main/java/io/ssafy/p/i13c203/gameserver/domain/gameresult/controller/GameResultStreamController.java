package io.ssafy.p.i13c203.gameserver.domain.gameresult.controller;

import io.ssafy.p.i13c203.gameserver.auth.security.CustomUserDetails;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.response.SummaryDto;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Tag(name = "게임 결과 조회 API (SSE)", description = "게임 결과 비동기 생성/구독 API")
@RequestMapping("/api/v1/game-results")
public interface GameResultStreamController {

    @Operation(
            summary = "AI 요약 SSE 구독",
            description = """
                    지정한 게임의 결과 요약을 비동기로 생성하여 SSE로 전송합니다.
                    - started: 스트림 시작
                    - progress: 진행 상황(프롬프트 구성, 호출, 파싱, 저장 등)
                    - ready: 최종 결과(SummaryDto) 전송
                    - pending/processing: 중간 상태 알림
                    - error: 오류 메시지
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "SSE 연결 성공",
                            content = @Content(mediaType = MediaType.TEXT_EVENT_STREAM_VALUE)),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "결과/게임 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @GetMapping(value = "/{gameId}/report/summary/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<APIResponse<SummaryDto, Void>>> stream(
            @Parameter(description = "게임 ID", example = "1")
            @PathVariable Long gameId,
            @Parameter(description = "프롬프트 해시", example = "ph_1")
            @RequestParam(name = "promptHash", required = false) String promptHash,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
