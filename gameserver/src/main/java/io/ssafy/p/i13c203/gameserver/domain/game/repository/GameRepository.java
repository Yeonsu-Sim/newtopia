package io.ssafy.p.i13c203.gameserver.domain.game.repository;

import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import java.util.List;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findFirstByMemberIdAndActiveTrueOrderByCreatedAtDesc(Long memberId);

    @Query("select g.id from Game g where g.memberId = :memberId")
    List<Long> findGameIdsByMemberId(@Param("memberId") Long memberId);
    boolean existsByIdAndMemberId(Long id, Long memberId);  // 소유권 확인

    @Query("""
        select g.endingCode as code,
               count(g) as cnt,
               max(g.updatedAt) as lastCollectedAt
        from Game g
        where g.memberId = :memberId
        group by g.endingCode
        having g.endingCode is not null
        """
    )
    List<EndingCountProjection> countEndingsByMember(@Param("memberId") Long memberId);

    interface EndingCountProjection {
        String getCode();
        int getCnt();
        LocalDateTime getLastCollectedAt();
    }
}