package io.ssafy.p.i13c203.gameserver.domain.game.doc;

import io.ssafy.p.i13c203.gameserver.domain.gameresult.doc.AppliedDoc;
import java.time.Instant;
import lombok.Builder;

@Builder
public record HistoryEntryDoc(
        int turn,
        String choosedCode,
        CountryStatsDoc countryStats,
        ChoiceWeightsDoc choiceWeights,
        CardDoc card,
        Instant finalizedAt,

        // 추가됨
        AppliedDoc applied,
        String choosedLabel
) {}