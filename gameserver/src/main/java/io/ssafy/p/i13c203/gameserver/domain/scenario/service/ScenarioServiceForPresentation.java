package io.ssafy.p.i13c203.gameserver.domain.scenario.service;

import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
import io.ssafy.p.i13c203.gameserver.domain.scenario.repository.ScenarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScenarioServiceForPresentation {
    private final ScenarioRepository scenarioRepository;

    //대기업 환경
    public Scenario getFirst(){
        return scenarioRepository.findById(1345L).orElseThrow(() -> new RuntimeException("시연 시나리오 1 에러 "));
    }

    // 배구
    public Scenario getSecond(){
        return scenarioRepository.findById(1340L).orElseThrow(() -> new RuntimeException("시연 시나리오 1 에러 "));
    }

}
