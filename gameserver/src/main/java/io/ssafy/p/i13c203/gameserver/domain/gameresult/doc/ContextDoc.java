package io.ssafy.p.i13c203.gameserver.domain.gameresult.doc;

import io.ssafy.p.i13c203.gameserver.domain.game.doc.CountryStatsDoc;

public record ContextDoc(
        String countryName,
        int finalTurnNumber,
        String generatedAt,       // ISO-8601
        CountryStatsDoc countryStats
) {}
