package io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.response;

import java.util.List;

public record GameResultReportResponse(
        Context context,
        Summary summary,
        Graph graph
) {
    public record Context(
            String countryName,
            int finalTurnNumber,
            String generatedAt,
            CountryStats countryStats
    ) {}

    public record CountryStats(
            int economy,
            int defense,
            int publicSentiment,
            int environment
    ) {}

    public record Summary(
            String status,      // pending | ready | error
            String promptHash,
            Sections sections,
            String subscribeUrl
    ) {}

    public record Sections(
            Section economy,
            Section defense,
            Section publicSentiment,
            Section environment
    ) {}

    public record Section(
            List<String> bullets
    ) {}

    public record Graph(
            List<String> metrics,
            Page page,
            List<Series> series
    ) {}

    public record Page(
            int number,
            int size,
            String sort,
            int totalElements,
            int totalPages,
            boolean hasNext,
            boolean hasPrev,
            Integer nextPage,
            Integer prevPage
    ) {}

    public record Series(
            String metric,
            List<Point> points
    ) {}

    public record Point(
            int turnNumber,
            int value
    ) {}
}
