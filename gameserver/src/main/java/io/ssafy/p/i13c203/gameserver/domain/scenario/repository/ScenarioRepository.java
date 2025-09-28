package io.ssafy.p.i13c203.gameserver.domain.scenario.repository;

import io.ssafy.p.i13c203.gameserver.domain.game.model.CardType;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
import java.util.List;
import java.util.Optional;
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

    @Query(value = "SELECT * FROM scenario WHERE id = 7", nativeQuery = true)
    Scenario findHardCodingEventScenario();


    @EntityGraph(attributePaths = "npc")
    @Query("""
       select s
       from Scenario s
       where s.type = :type
         and ( :#{#excludeIds == null || #excludeIds.isEmpty()} = true
               or s.id not in :excludeIds )
       order by function('RANDOM')
       """)
    List<Scenario> findRandomByTypeExcluding(
            @Param("type") CardType cardType,
            @Param("excludeIds") List<Long> excludeIds,
            Pageable pageable
    );

    @Query(value = """
        select s.*
        from scenario s
        where s.type = :type
          and s.id not in (:excludeIds)
          and (
            s.spawn is null
            or jsonb_typeof(s.spawn->'conditions') is null
            or not exists (
                select 1
                from jsonb_array_elements(coalesce(s.spawn->'conditions','[]'::jsonb)) as c
                where
                  (
                    (c->>'category') = 'economy' and
                    (
                      (c->>'operator') = 'LESS_THAN'  and :economy >= (c->>'threshold')::numeric
                      or
                      (c->>'operator') = 'MORE_THAN'  and :economy <= (c->>'threshold')::numeric
                    )
                  ) or
                  (
                    (c->>'category') = 'defense' and
                    (
                      (c->>'operator') = 'LESS_THAN'  and :defense >= (c->>'threshold')::numeric
                      or
                      (c->>'operator') = 'MORE_THAN'  and :defense <= (c->>'threshold')::numeric
                    )
                  ) or
                  (
                    (c->>'category') = 'environment' and
                    (
                      (c->>'operator') = 'LESS_THAN'  and :environment >= (c->>'threshold')::numeric
                      or
                      (c->>'operator') = 'MORE_THAN'  and :environment <= (c->>'threshold')::numeric
                    )
                  ) or
                  (
                    (c->>'category') = 'publicSentiment' and
                    (
                      (c->>'operator') = 'LESS_THAN'  and :publicSentiment >= (c->>'threshold')::numeric
                      or
                      (c->>'operator') = 'MORE_THAN'  and :publicSentiment <= (c->>'threshold')::numeric
                    )
                  )
            )
          )
        order by random()
        limit 1
        """, nativeQuery = true)
    Optional<Scenario> findOneEligibleRandomExcluding(
            @Param("type") String type,
            @Param("economy") int economy,
            @Param("defense") int defense,
            @Param("environment") int environment,
            @Param("publicSentiment") int publicSentiment,
            @Param("excludeIds") List<Long> excludeIds
    );
}