package io.ssafy.p.i13c203.gameserver.domain.ranking.dto;

import io.ssafy.p.i13c203.gameserver.domain.ranking.entity.Ranking;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;

@Builder
public record RankingDto(
  @Schema(example = "1")
  Long gameId,
  @Schema(example = "영호의 아기자기 왕국")
  String countryName,
  @Schema(example = "15")
  Integer turn,
  Instant endedAt,
  @Schema(example = "3")
  Long order) {

  public static RankingDto from(Ranking ranking, Long order) {
    var game = ranking.getGame();
    return RankingDto.builder()
      .gameId(game.getGameId())
      .countryName(game.getCountryName())
      .turn(game.getTurn())
      .endedAt(game.getEndedAt())
      .order(order)
      .build();
  }
}
