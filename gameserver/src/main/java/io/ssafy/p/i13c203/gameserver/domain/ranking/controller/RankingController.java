package io.ssafy.p.i13c203.gameserver.domain.ranking.controller;

import io.ssafy.p.i13c203.gameserver.domain.ranking.dto.RankingDto;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "랭킹 API", description = "게임 랭킹 관련 API")
public interface RankingController {

  @Operation(summary = "내 게임 랭킹 조회", description = "로그인한 사용자의 모든 게임 기록에 대한 랭킹을 조회합니다.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "랭킹 조회 성공"),
    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
  })
  ResponseEntity<APIResponse< List<RankingDto>, Void>> getMyGamesRanking(
    @Parameter(description = "회원 ID", required = true) Long memberId
  );

  @Operation(summary = "상위 N개 게임 랭킹 조회", description = "전체 게임 중 상위 N개의 랭킹을 조회합니다. 로그인 없이도 사용 가능 합니다.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "랭킹 조회 성공"),
    @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
  })
  ResponseEntity<APIResponse< List<RankingDto>, Void>> getTopNGamesRanking(
    @Parameter(description = "조회할 상위 랭킹 개수", required = true, example = "100") Integer topN
  );

  @Operation(summary = "특정 게임 랭킹 조회", description = "게임 ID를 기반으로 해당 게임의 랭킹 정보를 조회합니다. 로그인 없이도 사용 가능 합니다.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "랭킹 조회 성공"),
    @ApiResponse(responseCode = "404", description = "게임을 찾을 수 없음")
  })
  ResponseEntity<APIResponse<RankingDto, Void>> getRankingByGameId(
    @Parameter(description = "게임 ID", required = true) Long gameId
  );
}
