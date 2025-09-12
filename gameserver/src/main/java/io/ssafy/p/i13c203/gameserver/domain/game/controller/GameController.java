package io.ssafy.p.i13c203.gameserver.domain.game.controller;

import io.ssafy.p.i13c203.gameserver.auth.annotation.CurrentMemberId;
import io.ssafy.p.i13c203.gameserver.domain.game.dto.*;
import io.ssafy.p.i13c203.gameserver.domain.game.dto.request.CreateGameRequest;
import io.ssafy.p.i13c203.gameserver.domain.game.dto.request.SubmitChoiceRequest;
import io.ssafy.p.i13c203.gameserver.domain.game.dto.response.*;
import io.ssafy.p.i13c203.gameserver.domain.game.service.GameService;
import io.ssafy.p.i13c203.gameserver.domain.game.service.SubmitChoiceResult;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    // 진행 중인 게임 조회
    @GetMapping("/me")
    public ResponseEntity<APIResponse<GetMyGameResponse, Void>> getMyGame(@CurrentMemberId Long memberId) {
        var opt = gameService.getActiveGame(memberId);
        if (opt.isEmpty()) {
            return ResponseEntity.ok(APIResponse.success("진행 중인 게임이 없습니다.",
                    GetMyGameResponse.of(null)));
        }
        var g = opt.get();
        return ResponseEntity.ok(APIResponse.success("진행 중인 게임을 불러왔습니다.",
                GetMyGameResponse.of(
                        GameSummaryDto.of(
                                g.getGameId(),
                                g.getCountryName(),
                                TurnSummaryDto.of(
                                        g.getTurn(),
                                        CountryStatsDto.of(g.getCountryStats())
                                )
                        )
                )));
    }

    // 게임 만들기 (force=true면 기존 active를 닫고 새로 생성)
    @PostMapping
    public ResponseEntity<APIResponse<GameDetailResponse, Void>> createGame(
            @CurrentMemberId Long memberId,
            @RequestParam(name = "force", defaultValue = "false") boolean force,
            @RequestBody @Valid CreateGameRequest request
    ) {
        var game = gameService.createGame(memberId, request.countryName(), force);
        return ResponseEntity.ok(APIResponse.success("게임이 생성되었습니다.",
                GameDetailResponse.from(game)));
    }

    // 게임 상세 조회
    @GetMapping("/{gameId}")
    public ResponseEntity<APIResponse<GameDetailResponse, Void>> getGame(
            @PathVariable Long gameId,
            @CurrentMemberId Long memberId
    ) {
        var game = gameService.findByIdOrThrow(gameId, memberId);
        return ResponseEntity.ok(APIResponse.success("게임을 불러왔습니다.", GameDetailResponse.from(game)));
    }

    // 시나리오 답변 선택 (Idempotency-Key는 AOP가 헤더에서 읽음)
    @PostMapping("/{gameId}/choice")
    public ResponseEntity<APIResponse<SubmitChoiceResponse, Void>> submitChoice(
            @PathVariable Long gameId,
            @CurrentMemberId Long memberId,
            @RequestBody @Valid SubmitChoiceRequest request
    ) {
        SubmitChoiceResult result = gameService.submitChoice(
                gameId,
                memberId,
                request.cardId(),
                request.choice()
        );

        // applied.after & delta 구성
        var after = CountryStatsDto.of(result.nextTurn().countryStats());
        var prev = result.applied().countryStats();
        var delta = CountryStatsDeltaDto.of(
                after.economy() - prev.economy(),
                after.defense() - prev.defense(),
                after.publicSentiment() - prev.publicSentiment(),
                after.environment() - prev.environment()
        );

        // 게임 종료 판정/엔딩은 추후 서비스 레이어에서 결정하도록 훅만 남김
        var response = SubmitChoiceResponse.of(
                AppliedDto.of(
                        result.applied().turn(),
                        result.nextTurn().card().cardId(),
                        result.applied().choosedCode(),
                        CountryStatsChangeDto.of(after, delta)
                ),
                GameStateDto.of(false, null, null), // TODO: 서비스 로직 연동해 실제 종료 판단
                NextTurnDto.of(
                        result.nextTurn().turn(),
                        CountryStatsDto.of(result.nextTurn().countryStats()),
                        CardBriefDto.from(result.nextTurn().card())
                )
        );

        String msg = response.gameState().gameOver() ? "게임이 종료되었습니다." : "답변 선택을 완료했습니다.";
        return ResponseEntity.ok(APIResponse.success(msg, response));
    }
}
