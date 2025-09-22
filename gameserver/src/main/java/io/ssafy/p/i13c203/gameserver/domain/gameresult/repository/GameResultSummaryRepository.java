package io.ssafy.p.i13c203.gameserver.domain.gameresult.repository;

import io.ssafy.p.i13c203.gameserver.domain.gameresult.entity.GameResultSummary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameResultSummaryRepository extends JpaRepository<GameResultSummary, Long> {

    Optional<GameResultSummary> findByGameResultId(Long gameResultId);

    /**
     * 없으면 PENDING으로 생성, 있으면 아무 것도 하지 않음.
     * @return 1이면 새로 생성됨, 0이면 이미 존재함
     */
    @Modifying(clearAutomatically = true)
    @Query(value = """
    INSERT INTO game_result_summary (game_result_id, summary, created_at, updated_at)
    VALUES (:grId, CAST(:summaryJson AS jsonb), now(), now())
    ON CONFLICT (game_result_id) DO NOTHING
    """, nativeQuery = true)
    int insertPendingIfAbsent(@Param("grId") Long grId,
                              @Param("summaryJson") String summaryJson);

    /**
     * 현재 상태가 ERROR인 경우에만 PENDING으로 재설정.
     * @return 변경된 행 수(1이면 재시작됨, 0이면 재시작 조건 불충족)
     */
    @Modifying(clearAutomatically = true)
    @Query(value = """
    UPDATE game_result_summary
    SET summary    = CAST(:summaryJson AS jsonb),
        updated_at = now()
    WHERE game_result_id = :grId
      AND (summary->>'status') = 'ERROR'
    """, nativeQuery = true)
    int restartIfError(@Param("grId") Long grId,
                       @Param("summaryJson") String summaryJson);

}
