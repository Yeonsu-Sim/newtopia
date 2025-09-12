package io.ssafy.p.i13c203.gameserver.domain.game.doc;

public record CountryStatsDoc(
        int economy,
        int defense,
        int publicSentiment,
        int environment
) {}