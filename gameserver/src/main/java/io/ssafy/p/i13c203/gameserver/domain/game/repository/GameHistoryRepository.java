package io.ssafy.p.i13c203.gameserver.domain.game.repository;

import io.ssafy.p.i13c203.gameserver.domain.game.entity.GameHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameHistoryRepository extends JpaRepository<GameHistory, Long> {

    int countByGameId(Long gameId);

    Optional<GameHistory> findTopByGameIdOrderByTurnDesc(Long gameId);

    Optional<GameHistory> findByGameIdAndTurn(Long gameId, int turn);

    List<GameHistory> findAllByGameIdAndTurnBetweenOrderByTurnAsc(
            Long gameId, int startTurn, int endTurn
    );

    @Query(value = """
                select distinct (gh.entry->'card'->>'scenarioId')::bigint
                from game_history gh
                where gh.game_id in (:gameIds)
                  and jsonb_exists(gh.entry, 'card')
                  and jsonb_exists(gh.entry->'card', 'scenarioId')
            """, nativeQuery = true)
    List<Long> findDistinctScenarioIdsByGameIds(@Param("gameIds") List<Long> gameIds);

    // 전체 턴 기록 조회 (오름차순)
    List<GameHistory> findByGameIdOrderByTurnAsc(Long gameId);

}