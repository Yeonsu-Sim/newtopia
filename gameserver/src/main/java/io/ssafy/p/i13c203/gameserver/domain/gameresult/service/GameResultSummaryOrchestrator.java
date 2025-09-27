package io.ssafy.p.i13c203.gameserver.domain.gameresult.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.doc.SummaryDoc;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.entity.GameResultSummary;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.event.SummaryEventBus;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.model.SummaryStatus;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.repository.GameResultRepository;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.repository.GameResultSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameResultSummaryOrchestrator {

    private final GameResultRepository gameResultRepo;
    private final GameResultSummaryRepository summaryRepo;
    private final GameResultSummaryWorker worker;
    private final SummaryEventBus eventBus;
    private final ObjectMapper objectMapper; // SummaryDoc → JSON 직렬화용

    private static final String SUBS_URL_FORMAT = "/api/v1/game-results/%s/report/summary/stream?promptHash=%s";

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SummaryDoc ensureJob(Long gameId, String promptHash) {
        Long grId = gameResultRepo.findByGameId(gameId).orElseThrow().getId();


        // PENDING 문서 준비
        promptHash = normPromptHash(gameId, promptHash);
        String finalPromptHash = promptHash;
        String subscribeUrl = String.format(SUBS_URL_FORMAT, gameId, promptHash);
        SummaryDoc pendingDoc = new SummaryDoc(SummaryStatus.PENDING, promptHash, null, subscribeUrl);
        String pendingJson = toJson(pendingDoc);

        // 1) 없으면 생성(원자적 삽입)
        int created = summaryRepo.insertPendingIfAbsent(grId, pendingJson);

        // 2) 생성되지 않았다면, ERROR 상태 또는 오래된 PENDING 상태일 때만 재시작
        int restarted = 0;
        if (created == 0) {
            restarted = summaryRepo.restartIfError(grId, pendingJson);

            // === 오래된 PENDING 상태 체크 ===
            summaryRepo.findByGameResultId(grId).ifPresent(grs -> {
                SummaryDoc summary = grs.getSummary();
                if (summary != null
                        && summary.status() == SummaryStatus.PENDING
                        && grs.getUpdatedAt().isBefore(LocalDateTime.now().minusSeconds(30))) {
                    log.warn("Restarting stuck PENDING job. gameId={}, grId={}", gameId, grId);
                    worker.startAsync(gameId, grId, finalPromptHash);
                }
            });
        }

        // 생성 or 재시작된 경우에만, "커밋 후" 워커/이벤트 기동
        if (created > 0 || restarted > 0) {
            runAfterCommit(() -> {
                log.info("Ensure job has been successfully started. gameId={}, grId={}", gameId, grId);
                eventBus.publish(gameId, SummaryStatus.PENDING);
                worker.startAsync(gameId, grId, finalPromptHash);
            });
            return pendingDoc;
        } else {
            log.info("No-op (already running or ready). Skip starting worker. gameId={}, grId={}", gameId, grId);
        }

        // 최종 상태 조회 후 반환 (READY/PROCESSING/PENDING/ERROR 중 현재 값)
        return summaryRepo.findByGameResultId(grId)
                .map(GameResultSummary::getSummary)
                .orElseThrow();
    }

    private String normPromptHash(long gameId, String promptHash) {
        return promptHash == null ||  promptHash.isEmpty() ? "ph_" + gameId : promptHash;
    }

    private void runAfterCommit(Runnable r) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            log.info("[ensureJob] register afterCommit");
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() { r.run(); }
            });
        } else {
            log.warn("[ensureJob] tx sync inactive. running immediately.");
            r.run();
        }
    }

    private String toJson(SummaryDoc doc) {
        try {
            return objectMapper.writeValueAsString(doc);
        } catch (Exception e) {
            throw new IllegalStateException("SummaryDoc 직렬화 실패", e);
        }
    }
}
