package io.ssafy.p.i13c203.gameserver.domain.game.repository;

import io.ssafy.p.i13c203.gameserver.domain.game.entity.GameHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameHistoryRepository extends JpaRepository<GameHistory, Long> {
    List<GameHistory> findByGameIdOrderByTurnAsc(Long gameId);
}