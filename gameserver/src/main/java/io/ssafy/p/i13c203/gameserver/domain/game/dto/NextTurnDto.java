package io.ssafy.p.i13c203.gameserver.domain.game.dto;

public record NextTurnDto(
        int number,
        CountryStatsDto countryStats,
        CardBriefDto card
) {
    public static NextTurnDto of(int num, CountryStatsDto stats, CardBriefDto card) {
        return new NextTurnDto(num, stats, card);
    }
}
