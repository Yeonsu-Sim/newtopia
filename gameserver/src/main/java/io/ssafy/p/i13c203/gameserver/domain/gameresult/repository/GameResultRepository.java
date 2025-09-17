package io.ssafy.p.i13c203.gameserver.domain.gameresult.repository;

import io.ssafy.p.i13c203.gameserver.domain.gameresult.entity.GameResult;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameResultRepository extends JpaRepository<GameResult, Long> {
    Optional<GameResult> findByGameId(Long gameId);
}

