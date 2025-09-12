package io.ssafy.p.i13c203.gameserver.domain.game.dto;

public record TurnSummaryDto(
        int number, CountryStatsDto countryStats
) {
    public static TurnSummaryDto of(int number, CountryStatsDto stats) {
        return new TurnSummaryDto(number, stats);
    }
}
