package io.ssafy.p.i13c203.gameserver.domain.gameresult.ai;

import java.util.List;

public interface GameResultSummaryPromptBuilder {
    String systemPrompt();
    String userPrompt(List<String> gameHistoryEntries);
}
