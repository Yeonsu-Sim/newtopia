package io.ssafy.p.i13c203.gameserver.domain.scenario.service;

import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;

public interface EventScenarioService {

    /**
     * 다음 이벤트 시나리오를 선별한다.
     */
    Scenario nextScenario();

    Scenario nextScenarioForPresentation();

}
