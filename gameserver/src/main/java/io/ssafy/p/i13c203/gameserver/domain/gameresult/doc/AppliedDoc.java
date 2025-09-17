package io.ssafy.p.i13c203.gameserver.domain.gameresult.doc;

import io.ssafy.p.i13c203.gameserver.domain.game.doc.CountryStatsDoc;

public record AppliedDoc(
        CountryStatsDoc before,
        CountryStatsDoc after,
        CountryStatsDoc delta
) {
    public static AppliedDoc of(CountryStatsDoc before, CountryStatsDoc after) {
        if (before == null || after == null) {
            return new AppliedDoc(before,after,null);
        }

        CountryStatsDoc delta = new CountryStatsDoc(
                after.economy() - before.economy(),
                after.defense() - before.defense(),
                after.publicSentiment() -  before.publicSentiment(),
                after.environment() - before.environment()
        );
        return new AppliedDoc(
                before,after,delta
        );
    }
}
