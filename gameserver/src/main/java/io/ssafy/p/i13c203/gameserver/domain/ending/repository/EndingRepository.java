package io.ssafy.p.i13c203.gameserver.domain.ending.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import io.ssafy.p.i13c203.gameserver.domain.ending.entity.Ending;

public interface EndingRepository extends JpaRepository<Ending, Long> {
    @EntityGraph(attributePaths = "image")
    Optional<Ending> findByCode(String code);

    List<Ending> findAllByOrderByIdAsc();
}
