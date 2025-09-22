package io.ssafy.p.i13c203.gameserver.domain.gameresult.controller;

import io.ssafy.p.i13c203.gameserver.auth.security.CustomUserDetails;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.reader.GameResultReader;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.reader.GameResultSummaryReader;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.mapper.GameResultSummaryDtoMapper;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.response.SummaryDto;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.event.SummaryEventBus;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.service.GameResultSummaryOrchestrator;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.internal.http2.ErrorCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/game-results")
public class GameResultStreamControllerImpl implements GameResultStreamController {

    private final GameResultReader gameResultReader;  // 소유권/권한 확인
    private final GameResultSummaryReader gameResultSummaryReader;

    private final GameResultSummaryOrchestrator orchestrator;
    private final SummaryEventBus eventBus;
    private final GameResultSummaryDtoMapper summaryMapper;

    @GetMapping(value = "/{gameId}/report/summary/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<APIResponse<SummaryDto,Void>>> stream(
            @PathVariable Long gameId,
            @RequestParam(required = false) String promptHash,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // 1) 인증/인가
        final Long memberId = (userDetails != null) ? userDetails.getMemberId() : null;
        if (memberId == null) throw new AccessDeniedException("인증이 필요합니다.");
        if (!gameResultReader.existsOwnedBy(gameId, memberId)) throw new AccessDeniedException("해당 리소스를 조회할 권한이 없습니다.");

        // A) 잡 보장(필요시 생성 후 비동기 시작)
        var ensure = orchestrator.ensureJob(gameId, promptHash);
        log.info("summary ensuredJob={}", ensure);

        // B) 즉시 스냅샷 1건
        Mono<ServerSentEvent<APIResponse<SummaryDto,Void>>> current =
                Mono.fromSupplier(() -> {
                    var row = gameResultSummaryReader.snapshot(gameId).orElseThrow();
                    var dto = (row != null) ? summaryMapper.toSummary(row.getSummary()) : null;
                    return sse("current", ok("snapshot", dto));
                });

        // C) 상태 전이 구독: Many → Flux 변환 후 map
        Flux<ServerSentEvent<APIResponse<SummaryDto,Void>>> updates =
                eventBus.channel(gameId)
                        .asFlux()
                        .map(evt -> {
                            var row = gameResultSummaryReader.snapshot(gameId).orElseThrow();

                            var dto = switch (evt.status()) {
                                case READY -> summaryMapper.toReadySummary(row.getSummary());
                                default -> summaryMapper.toSummary(row.getSummary());
                            };
                            return switch (evt.status()) {
                                case PENDING    -> sse("pending", ok("게임 결과 요약 생성에 대기중입니다.", dto));
                                case PROCESSING -> sse("processing", ok("게임 결과 요약을 생성중입니다.", dto));
                                case READY      -> sse("ready", ok("게임 결과 요약 생성이 완료되었습니다.", dto));
                                case ERROR      -> sse("error", error("게임 결과 요약 생성에 실패했습니다."));
                            };
                        });

        // D) 하트비트
        Flux<ServerSentEvent<APIResponse<SummaryDto,Void>>> heartbeat =
                Flux.interval(Duration.ofSeconds(15))
                        .map(i -> sse("heartbeat", ok("ping", null)));

        // E) 동일 제네릭으로 맞추기
        Mono<ServerSentEvent<APIResponse<SummaryDto,Void>>> started =
                Mono.just(sse("started", ok("started", null)));

        return Flux.concat(started, current)
                .mergeWith(updates)
                .mergeWith(heartbeat);
    }

    // ===== helpers =====
    private static <T> ServerSentEvent<APIResponse<T,Void>> sse(String event, APIResponse<T,Void> data) {
        return ServerSentEvent.<APIResponse<T,Void>>builder()
                .event(event)
                .data(data)
                .build();
    }

    // 모든 곳에서 같은 제네릭을 쓰도록 고정
    private static APIResponse<SummaryDto,Void> ok(String msg, SummaryDto data) {
        return APIResponse.<SummaryDto,Void>success(msg, data);

    }private static APIResponse<SummaryDto,Void> error(String msg) {
        return APIResponse.<SummaryDto,Void>fail(ErrorCode.INTERNAL_ERROR.name(), msg);
    }

}
