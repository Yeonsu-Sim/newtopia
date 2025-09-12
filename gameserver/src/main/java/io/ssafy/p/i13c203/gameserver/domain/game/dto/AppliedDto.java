package io.ssafy.p.i13c203.gameserver.domain.game.dto;

import java.util.UUID;

public record AppliedDto(
        int turnNumber,
        UUID cardId,
        String choiceCode,
        CountryStatsChangeDto countryStats
) {
    public static AppliedDto of(int turn, UUID cardId, String code, CountryStatsChangeDto stats) {
        return new AppliedDto(turn, cardId, code, stats);
    }
}
