package io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.response;

public record GameResultReportResponse(
        ContextDto context,
        SummaryDto summary,
        GraphDto graph
) {}
