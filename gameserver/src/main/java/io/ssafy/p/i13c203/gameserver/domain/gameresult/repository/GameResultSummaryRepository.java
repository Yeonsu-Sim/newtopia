package io.ssafy.p.i13c203.gameserver.domain.gameresult.repository;

import io.ssafy.p.i13c203.gameserver.domain.gameresult.entity.GameResultSummary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameResultSummaryRepository extends JpaRepository<GameResultSummary, Long> {
    Optional<GameResultSummary> findByGameResultId(Long gameResultId);
}
