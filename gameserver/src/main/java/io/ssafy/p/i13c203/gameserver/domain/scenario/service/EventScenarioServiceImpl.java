package io.ssafy.p.i13c203.gameserver.domain.scenario.service;

import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
import io.ssafy.p.i13c203.gameserver.domain.scenario.repository.ScenarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventScenarioServiceImpl implements EventScenarioService {

    private final ScenarioRepository scenarioRepository;

    /**
     * 다음 이벤트 시나리오를 선별한다.
     */
    @Override
    public Scenario nextScenario() {
        // TODO: HARD CODING for Presentation
        // return scenarioRepository.findRandomEventScenario();
        return scenarioRepository.findHardCodingEventScenario();
    }

}
