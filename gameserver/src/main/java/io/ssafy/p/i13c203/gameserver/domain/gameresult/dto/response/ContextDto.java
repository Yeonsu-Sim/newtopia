package io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.response;

public record ContextDto (
        String countryName,
        int finalTurnNumber,
        String generatedAt,
        CountryStats countryStats
) {
    public record CountryStats(
            int economy,
            int defense,
            int publicSentiment,
            int environment
    ) {}
}
