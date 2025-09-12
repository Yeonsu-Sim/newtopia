package io.ssafy.p.i13c203.gameserver.domain.ending.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import io.ssafy.p.i13c203.gameserver.domain.ending.entity.Ending;

public interface EndingRepository extends JpaRepository<Ending, Long> {
    Optional<Ending> findByCode(String code);
    boolean existsByCode(String code);
}
