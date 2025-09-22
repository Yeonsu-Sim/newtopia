package io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.response;

import java.util.List;

public record GraphDto(
        List<String> metrics,
        Page page,
        List<Series> series
) {
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