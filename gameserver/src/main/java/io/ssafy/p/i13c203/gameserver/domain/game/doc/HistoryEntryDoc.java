package io.ssafy.p.i13c203.gameserver.domain.game.doc;

import java.time.Instant;

public record HistoryEntryDoc(
        int turn,
        String choosedCode,
        CountryStatsDoc countryStats,
        ChoiceWeightsDoc choiceWeights,
        CardDoc card,
        Instant finalizedAt
) {}