package io.ssafy.p.i13c203.gameserver.domain.game.dto;

public record TurnDetailDto(
        int number,
        CountryStatsDto countryStats,
        CardBriefDto card
) {}
