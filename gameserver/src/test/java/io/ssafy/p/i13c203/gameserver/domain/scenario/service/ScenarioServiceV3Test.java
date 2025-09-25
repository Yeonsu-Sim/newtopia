package io.ssafy.p.i13c203.gameserver.domain.scenario.service;

import static org.assertj.core.api.Assertions.*;

import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ScenarioServiceV3Test {

    @Autowired
    private ScenarioServiceV3 scenarioServiceV3;

    @Test
    void firstScenario() {

        Scenario scenario = scenarioServiceV3.firstScenario(1L);

        assertThat(scenario.getId()).isNotNull();
        assertThat(scenario.getTitle()).isNotNull();
        assertThat(scenario.getArticleId()).isNotNull();
        assertThat(scenario.getNpc()).isNotNull();
        assertThat(scenario.getChoices()).isNotNull().hasSize(2);
        assertThat(scenario.getSpawn()).isNotNull();
        assertThat(scenario.getRelatedArticle()).isNotNull();
    }

    @Test
    void nextScenario() {
        Scenario scenario = scenarioServiceV3.firstScenario(1L);

        assertThat(scenario.getId()).isNotNull();
        assertThat(scenario.getTitle()).isNotNull();
        assertThat(scenario.getArticleId()).isNotNull();
        assertThat(scenario.getNpc()).isNotNull();
        assertThat(scenario.getChoices()).isNotNull().hasSize(2);
        assertThat(scenario.getSpawn()).isNotNull();
        assertThat(scenario.getRelatedArticle()).isNotNull();
    }
}