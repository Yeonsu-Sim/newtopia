package io.ssafy.p.i13c203.gameserver.domain.gameresult.service;

import io.ssafy.p.i13c203.gameserver.domain.gameresult.doc.ReportSummaryDoc;

public interface GameResultReportService {

    /**
     * 요약(summary)을 upsert 한다. (pending -> ready 등)
     */
    void upsertSummary(Long memberId, Long gameResultId, ReportSummaryDoc summary);
}