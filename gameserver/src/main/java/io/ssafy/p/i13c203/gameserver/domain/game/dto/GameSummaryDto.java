package io.ssafy.p.i13c203.gameserver.domain.game.dto;


public record GameSummaryDto(
        Long gameId,
        String countryName,
        TurnSummaryDto turnSummary
) {
    public static GameSummaryDto of(Long id, String countryName, TurnSummaryDto ts) {
        return new GameSummaryDto(id, countryName, ts);
    }
}
