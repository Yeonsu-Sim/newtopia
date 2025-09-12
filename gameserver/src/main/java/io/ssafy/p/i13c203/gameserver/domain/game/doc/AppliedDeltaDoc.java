package io.ssafy.p.i13c203.gameserver.domain.game.doc;

public record AppliedDeltaDoc(
        CountryStatsDoc after,
        CountryStatsDoc delta
) {}