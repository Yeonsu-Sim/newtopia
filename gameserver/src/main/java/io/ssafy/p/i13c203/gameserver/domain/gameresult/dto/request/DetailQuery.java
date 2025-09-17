package io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.request;

import java.util.List;

public record DetailQuery(
        // economy | defense | environment | publicSentiment
        List<Metric> metrics // null/empty → 전부
) {
}
