package io.ssafy.p.i13c203.gameserver.domain.scenario.repository;

import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.NewsEvent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NewsEventRepository extends JpaRepository<NewsEvent, String> {

    @Query(value = "SELECT * FROM news_events ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<NewsEvent> findRandom();
}
