package io.ssafy.p.i13c203.gameserver.domain.game.dto;

import io.ssafy.p.i13c203.gameserver.domain.game.doc.CountryStatsDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.model.CountryStats;

public record CountryStatsDto(
        int economy,
        int defense,
        int publicSentiment,
        int environment
) {
    public static CountryStatsDto of(CountryStats s) {
        return new CountryStatsDto(s.getEconomy(), s.getDefense(), s.getPublicSentiment(), s.getEnvironment());
    }

    public static CountryStatsDto of(CountryStatsDoc s) {
        return new CountryStatsDto(s.economy(), s.defense(), s.publicSentiment(), s.environment());
    }
}