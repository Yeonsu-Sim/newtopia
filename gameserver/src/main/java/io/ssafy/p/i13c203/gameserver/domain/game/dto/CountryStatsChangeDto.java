package io.ssafy.p.i13c203.gameserver.domain.game.dto;

public record CountryStatsChangeDto(
        CountryStatsDto after,
        CountryStatsDeltaDto delta
) {
    public static CountryStatsChangeDto of(CountryStatsDto after, CountryStatsDeltaDto delta) {
        return new CountryStatsChangeDto(after, delta);
    }
}
