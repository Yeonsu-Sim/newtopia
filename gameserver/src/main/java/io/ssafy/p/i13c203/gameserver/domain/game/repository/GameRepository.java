package io.ssafy.p.i13c203.gameserver.domain.game.repository;

import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findFirstByMemberIdAndActiveTrueOrderByCreatedAtDesc(Long memberId);
}