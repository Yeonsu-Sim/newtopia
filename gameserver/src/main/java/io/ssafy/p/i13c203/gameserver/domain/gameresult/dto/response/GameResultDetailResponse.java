package io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.response;

import io.ssafy.p.i13c203.gameserver.domain.game.model.CardType;
import java.util.List;

public record GameResultDetailResponse(
        Context context,
        Applied applied,
        Card card
) {
    public record Context(
            int turnNumber,
            int finalTurnNumber,
            List<String> metrics
    ) {}

    public record Applied(
            int turnNumber,
            String choiceCode,
            String choiceLabel,
            CountryStatsGroup countryStats
    ) {}

    public record CountryStatsGroup(
            CountryStats before,
            CountryStats after,
            CountryStats delta
    ) {}

    public record CountryStats(
            int economy,
            int defense,
            int publicSentiment,
            int environment
    ) {}

    public record Card(
            String title,
            String content,
            CardType type, // ORIGIN | CONSEQUENCE | ...
            Npc npc,
            List<Choice> choices,
            RelatedArticle relatedArticle
    ) {}

    public record Npc(
            String name,
            String imageUrl
    ) {}

    public record Choice(
            String code,
            String label
    ) {}

    public record RelatedArticle(
            String title,
            String url
    ) {}
}
