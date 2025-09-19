package io.ssafy.p.i13c203.gameserver.domain.gameresult.doc;

import java.util.List;

public record ReportSummaryDoc(
        String status,           // pending | ready | error
        String promptHash,
        Sections sections,       // null if pending/error
        String subscribeUrl
) {
    public record Sections(
            Section economy, Section defense, Section publicSentiment, Section environment
    ) {}
    public record Section(List<String> bullets) {}
}
