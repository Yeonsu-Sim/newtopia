package io.ssafy.p.i13c203.gameserver.domain.ranking.controller;

import io.ssafy.p.i13c203.gameserver.domain.ranking.dto.RankingDto;
import io.ssafy.p.i13c203.gameserver.domain.ranking.service.RankingService;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RankingControllerImpl implements RankingController {
  private final RankingService rankingService;

  @Override
  @GetMapping("/ranking/me")
  public ResponseEntity<APIResponse<List<RankingDto>, Void>> getMyGamesRanking(@AuthenticationPrincipal(expression = "memberId") Long memberId) {
    var rankings = rankingService.getRankingByMemberId(memberId);
    return ResponseEntity.ok(APIResponse.success(rankings));
  }

  @Override
  @GetMapping("/public/ranking/top")
  public ResponseEntity<APIResponse<List<RankingDto>, Void>> getTopNGamesRanking(@RequestParam(defaultValue = "100") Integer topN) {
    var rankings = rankingService.getRankingByTopN(topN);
    return ResponseEntity.ok(APIResponse.success(rankings));
  }

  @Override
  @GetMapping("/public/ranking/gameId/{gameId}")
  public ResponseEntity<APIResponse<RankingDto, Void>> getRankingByGameId(@PathVariable Long gameId) {
    var ranking = rankingService.getRankingByGameId(gameId);
    return ResponseEntity.ok(APIResponse.success(ranking));
  }
}
