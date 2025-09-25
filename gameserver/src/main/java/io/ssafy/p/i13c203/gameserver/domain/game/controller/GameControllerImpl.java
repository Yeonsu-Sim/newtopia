package io.ssafy.p.i13c203.gameserver.domain.game.controller;

import io.ssafy.p.i13c203.gameserver.auth.security.CustomUserDetails;
import io.ssafy.p.i13c203.gameserver.domain.game.dto.*;
import io.ssafy.p.i13c203.gameserver.domain.game.dto.request.CreateGameRequest;
import io.ssafy.p.i13c203.gameserver.domain.game.dto.request.SubmitChoiceRequest;
import io.ssafy.p.i13c203.gameserver.domain.game.dto.response.*;
import io.ssafy.p.i13c203.gameserver.domain.game.service.GameService;
import io.ssafy.p.i13c203.gameserver.domain.game.service.SubmitChoiceResult;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
public class GameControllerImpl implements GameController {

    private final GameService gameService;

    @Override
    public ResponseEntity<APIResponse<GetMyGameResponse, Void>> getMyGame(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).body(APIResponse.fail("AUTH_REQUIRED", "인증이 필요합니다"));
        }

        Long memberId = userDetails.getMemberId();
        var opt = gameService.getActiveGame(memberId);
        if (opt.isEmpty()) {
            return ResponseEntity.ok(APIResponse.success("진행 중인 게임이 없습니다.",
                    GetMyGameResponse.of(null)));
        }
        var g = opt.get();
        return ResponseEntity.ok(APIResponse.success("진행 중인 게임을 불러왔습니다.",
                GetMyGameResponse.of(
                        GameSummaryDto.of(
                                g.getId(),
                                g.getCountryName(),
                                TurnSummaryDto.of(
                                        g.getTurn(),
                                        CountryStatsDto.of(g.getCountryStats())
                                )
                        )
                )));
    }

    @Override
    public ResponseEntity<APIResponse<GameDetailResponse, Void>> createGame(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "force", defaultValue = "false") boolean force,
            @RequestBody @Valid CreateGameRequest request
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(APIResponse.fail("AUTH_REQUIRED", "인증이 필요합니다"));
        }

        Long memberId = userDetails.getMemberId();
        var game = gameService.createGame(memberId, request.countryName(), force);
        return ResponseEntity.ok(APIResponse.success("게임이 생성되었습니다.",
                GameDetailResponse.from(game)));
    }

    @Override
    public ResponseEntity<APIResponse<GameDetailResponse, Void>> getGame(
            @PathVariable Long gameId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(APIResponse.fail("AUTH_REQUIRED", "인증이 필요합니다"));
        }

        Long memberId = userDetails.getMemberId();
        var game = gameService.findByIdOrThrow(gameId, memberId);
        return ResponseEntity.ok(APIResponse.success("게임을 불러왔습니다.", GameDetailResponse.from(game)));
    }

    @Override
    public ResponseEntity<APIResponse<SubmitChoiceResponse, Void>> submitChoice(
            @PathVariable Long gameId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestHeader(value = "Idempotency-Key", required = false) String idemKey,
            @RequestBody @Valid SubmitChoiceRequest request
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(APIResponse.fail("AUTH_REQUIRED", "인증이 필요합니다"));
        }

        Long memberId = userDetails.getMemberId();

        SubmitChoiceResult result = gameService.submitChoice(
                gameId,
                memberId,
                request.cardId(),
                request.choice()
        );

        // after / prev / delta
        var afterDto = CountryStatsDto.of(result.nextTurn().countryStats()); // 적용 "이후"
        var prevDoc  = result.applied().countryStats();                      // 적용 "이전"
        var deltaDto = CountryStatsDeltaDto.of(
                afterDto.economy()        - prevDoc.economy(),
                afterDto.defense()        - prevDoc.defense(),
                afterDto.publicSentiment()- prevDoc.publicSentiment(),
                afterDto.environment()    - prevDoc.environment()
        );

        // 다음 카드(엔딩이면 null)
        var nextCard = result.nextTurn().card();
        CardBriefDto nextCardDto = (nextCard == null) ? null : CardBriefDto.from(nextCard);
        var nextCardIdForApplied = (nextCard == null) ? null : nextCard.cardId();

        var response = SubmitChoiceResponse.of(
                AppliedDto.of(
                        result.applied().turn(),          // 이번에 마무리된 턴 번호
                        nextCardIdForApplied,             // 다음 카드 id (엔딩이면 null)
                        result.applied().choosedCode(),   // 사용자가 고른 코드
                        CountryStatsChangeDto.of(afterDto, deltaDto)
                ),
                // 실제 게임 종료/엔딩 정보 반영
                GameStateDto.of(
                        result.gameOver(),
                        null,  // TODO: 미사용 필드 수거 보류
                        EndingDto.from(result.ending())
                ),
                NextTurnDto.of(
                        result.nextTurn().turn(),         // 다음 턴 번호(엔딩이면 현재 턴 유지)
                        afterDto,                         // 적용 이후 스탯
                        nextCardDto                       // 다음 카드(엔딩이면 null)
                )
        );

        String msg = result.gameOver() ? "게임이 종료되었습니다." : "답변 선택을 완료했습니다.";
        return ResponseEntity.ok(APIResponse.success(msg, response));
    }

    @Override
    public ResponseEntity<APIResponse<ChoiceHintDTO, Void>> getHints(Long gameId,
            CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(APIResponse.fail("AUTH_REQUIRED", "인증이 필요합니다"));
        }

        ChoiceHintDTO hintsAboutChoice = gameService.getHintsAboutChoice(gameId);

        return ResponseEntity.ok(APIResponse.success(hintsAboutChoice));
    }

}
