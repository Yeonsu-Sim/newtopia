package io.ssafy.p.i13c203.gameserver.domain.gameresult.doc;

import io.ssafy.p.i13c203.gameserver.domain.gameresult.model.SummarySections;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.model.SummaryStatus;

public record SummaryDoc (
        SummaryStatus status,       // pending | ready | error
        String promptHash,          // pending일 때만 의미 있음
        SummarySections sections,   // ready일 때만 채움
        String subscribeUrl         // pending일 때만 채움
) {}
