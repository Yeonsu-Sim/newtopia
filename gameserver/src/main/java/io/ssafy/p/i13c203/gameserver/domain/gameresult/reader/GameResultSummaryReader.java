package io.ssafy.p.i13c203.gameserver.domain.gameresult.reader;

import io.ssafy.p.i13c203.gameserver.domain.gameresult.entity.GameResultSummary;

import java.util.Optional;

public interface GameResultSummaryReader {

    /** gameId로 최신 요약 스냅샷을 DTO로 반환 */
    Optional<GameResultSummary> snapshot(Long gameId);
}

