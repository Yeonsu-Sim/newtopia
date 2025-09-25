package io.ssafy.p.i13c203.gameserver.domain.ranking.repository;

import io.ssafy.p.i13c203.gameserver.domain.ranking.entity.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface RankingRepository extends JpaRepository<Ranking, Long> {
  @Query("""
     SELECT r
     FROM Ranking r
     ORDER BY r.score DESC, r.rankingId ASC
  """)
  List<Ranking> findTopRankings(Pageable pageable);

  @Query(value = """
    WITH me AS (
      SELECT r.ranking_id AS rid,
             r.score      AS sc,
             g.id    AS gameId,
             g.country_name AS countryName,
             g.turn_number  AS turn,
             g.ended_at     AS endedAt
      FROM rankings r
      JOIN game g ON g.id = r.id
      WHERE r.id = :gameId
    )
    SELECT m.gameId,
           m.countryName,
           m.turn,
           m.endedAt,
           1 + (
             SELECT COUNT(*) FROM rankings r2
             WHERE r2.score > (SELECT sc FROM me)
                OR (r2.score = (SELECT sc FROM me) AND r2.ranking_id < (SELECT rid FROM me))
           ) AS "order"
    FROM me m
    """, nativeQuery = true)
  RankingDtoRow findRankingDtoRowByGameId(@Param("gameId") Long gameId);

  @Query(value = """
    SELECT
      g.id                             AS gameId,
      g.country_name                        AS countryName,
      g.turn_number                         AS turn,
      g.ended_at                            AS endedAt,
      RANK() OVER (ORDER BY r.score DESC, r.ranking_id ASC) AS ord
    FROM rankings r
    JOIN game g ON g.id = r.id
    WHERE g.member_id = :memberId
    ORDER BY ord ASC
    """, nativeQuery = true)
  List<RankingDtoRow> findUserRanks(@Param("memberId") Long memberId);

  interface RankingDtoRow {
    Long getGameId();
    String getCountryName();
    Integer getTurn();
    Instant getEndedAt();
    Long getOrder();
  }
}
