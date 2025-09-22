package io.ssafy.p.i13c203.gameserver.domain.gameresult.service;

import io.ssafy.p.i13c203.gameserver.common.ai.OpenAiClient;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.reader.GameHistoryReader;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.ai.GameResultSummaryPromptBuilder;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.ai.SummaryMessageParser;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.doc.SummaryDoc;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.event.SummaryEventBus;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.model.SummarySections;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.model.SummaryStatus;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.repository.GameResultSummaryRepository;
import io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode;
import io.ssafy.p.i13c203.gameserver.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class GameResultSummaryWorker {

    private final GameResultSummaryRepository summaryRepo;
    private final OpenAiClient openAiClient;
    private final GameResultSummaryPromptBuilder promptBuilder;
    private final GameHistoryReader historyReader;
    private final SummaryMessageParser parser;
    private final SummaryEventBus eventBus;

    private static final String MODEL = "gpt-4.1";

    @Async("taskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void startAsync(Long gameId, Long gameResultId, String promptHash) {
        markProcessing(gameResultId, promptHash);
        try {
            var entries = historyReader.readTop10Turns(gameId);
            var system = promptBuilder.systemPrompt();
            var user   = promptBuilder.userPrompt(entries);
            var text   = openAiClient.chatCompletion(MODEL, system, user);
            var sections = parser.parse(text);
            markReady(gameResultId, promptHash, sections);
            eventBus.publish(gameId, SummaryStatus.READY);
        } catch (Exception e) {
            markError(gameResultId, promptHash);
            eventBus.publish(gameId, SummaryStatus.ERROR);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void markProcessing(Long grId, String ph) {
        var row = summaryRepo.findByGameResultId(grId).orElseThrow(
                () -> new NotFoundException(ErrorCode.NOT_FOUND, "Game Result Id: " + grId)
        );
        row.setSummary(new SummaryDoc(SummaryStatus.PROCESSING, ph, null, row.getSummary().subscribeUrl()));
        summaryRepo.saveAndFlush(row);
        log.info("Mark processing for Game Result Id: {}", grId);
        eventBus.publish(row.getGameResultId(), SummaryStatus.PROCESSING);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void markReady(Long grId, String ph, SummarySections sections) {
        var row = summaryRepo.findByGameResultId(grId).orElseThrow(
                () -> new NotFoundException(ErrorCode.NOT_FOUND, "Game Result Id: " + grId)
        );
        row.setSummary(new SummaryDoc(SummaryStatus.READY, ph, sections, row.getSummary().subscribeUrl()));
        summaryRepo.saveAndFlush(row);
        log.info("Mark ready for Game Result Id: {}", grId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void markError(Long grId, String ph) {
        var row = summaryRepo.findByGameResultId(grId).orElseThrow(
                () -> new NotFoundException(ErrorCode.NOT_FOUND, "Game Result Id: " + grId)
        );
        row.setSummary(new SummaryDoc(SummaryStatus.ERROR, ph, null, row.getSummary().subscribeUrl()));
        summaryRepo.saveAndFlush(row);
        log.info("Mark error for Game Result Id: {}", grId);
    }
}
