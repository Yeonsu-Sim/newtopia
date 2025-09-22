package io.ssafy.p.i13c203.gameserver.domain.gameresult.reader;

import io.ssafy.p.i13c203.gameserver.domain.gameresult.entity.GameResultSummary;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.repository.GameResultRepository;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.repository.GameResultSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GameResultSummaryReaderImpl implements GameResultSummaryReader {

    private final GameResultRepository gameResultRepo;
    private final GameResultSummaryRepository summaryRepo;

    /** gameId로 최신 요약 스냅샷을 DTO로 반환 (읽기 전용 트랜잭션) */
    @Override
    @Transactional(readOnly = true)
    public Optional<GameResultSummary> snapshot(Long gameId) {
        Long grId = gameResultRepo.findByGameId(gameId).orElseThrow().getId();
        return summaryRepo.findByGameResultId(grId);
    }
}

