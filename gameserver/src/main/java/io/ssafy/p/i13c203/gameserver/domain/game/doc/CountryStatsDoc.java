package io.ssafy.p.i13c203.gameserver.domain.game.doc;

import io.ssafy.p.i13c203.gameserver.domain.game.model.CountryStats;

public record CountryStatsDoc(
        int economy,
        int defense,
        int publicSentiment,
        int environment
) {
    public static CountryStatsDoc from(CountryStats stats) {
        return new CountryStatsDoc(
                stats.getEconomy(),
                stats.getDefense(),
                stats.getPublicSentiment(),
                stats.getEnvironment()
        );
    }
}