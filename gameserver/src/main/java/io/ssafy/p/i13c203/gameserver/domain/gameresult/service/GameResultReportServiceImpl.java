package io.ssafy.p.i13c203.gameserver.domain.gameresult.service;

import io.ssafy.p.i13c203.gameserver.domain.gameresult.doc.ReportSummaryDoc;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.entity.GameResultSummary;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.adapter.GameResultReader;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.repository.GameResultRepository;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.repository.GameResultSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GameResultReportServiceImpl implements GameResultReportService {

    private final GameResultReader reader;                       // 권한/히스토리 조회 포트
    private final GameResultRepository gameResultRepository;     // context
    private final GameResultSummaryRepository summaryRepository; // summary

    @Override
    public void upsertSummary(Long memberId, Long gameId, ReportSummaryDoc summaryDoc) {
        ensureOwned(gameId, memberId);
        var gr = gameResultRepository.findByGameId(gameId)
                .orElseThrow(() -> notFound("결과가 존재하지 않습니다. (game_result)"));
        var s = summaryRepository.findByGameResultId(gr.getId()).orElse(null);
        if (s == null) {
            s = GameResultSummary.builder()
                    .gameResultId(gr.getId())
                    .summary(summaryDoc)
                    .build();
        } else {
            s.setSummary(summaryDoc);
        }
        summaryRepository.save(s);
    }

    // ===== Helpers =====

    private void ensureOwned(Long gameId, Long memberId) {
        if (!reader.existsOwnedBy(gameId, memberId)) {
            throw new IllegalStateException("해당 리소스를 처리할 수 없습니다."); // 프로젝트 커스텀 예외로 교체 권장
        }
    }

    private RuntimeException notFound(String msg) {
        return new java.util.NoSuchElementException(msg);
    }
}
