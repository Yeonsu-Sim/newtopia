package io.ssafy.p.i13c203.gameserver.domain.scenario.repository;

import io.ssafy.p.i13c203.gameserver.domain.game.model.CardType;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

    // type 컬럼이 EVENT인 시나리오 중 랜덤 1개 가져오기
    @Query(value =
        """
        SELECT * FROM scenario
        WHERE type = 'EVENT'
        ORDER BY random() LIMIT 1
        """
        , nativeQuery = true)
    Scenario findRandomEventScenario();


    @EntityGraph(attributePaths = "npc")
    @Query("""
       select s 
       from Scenario s
       where s.type = :type
       order by function('RANDOM')
       """)
    List<Scenario> findRandomByType(@Param("type") CardType cardType, Pageable pageable);

}