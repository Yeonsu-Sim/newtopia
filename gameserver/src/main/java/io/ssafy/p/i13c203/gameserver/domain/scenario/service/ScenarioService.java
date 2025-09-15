package io.ssafy.p.i13c203.gameserver.domain.scenario.service;

import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
import io.ssafy.p.i13c203.gameserver.domain.scenario.repository.ScenarioRepository;
import io.ssafy.p.i13c203.gameserver.global.exception.BusinessException;
import io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScenarioService {
    private final ScenarioRepository scenarioRepo;


    /** 게임 시작 시 선택할 첫 시나리오를 반환 */
    public Scenario firstScenario() {
        return scenarioRepo.findAll()
                .stream().findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.SCENARIO_NOT_FOUND));
    }

    /** 다음 턴에 사용할 시나리오를 반환 (등장 조건 평가는 이 메서드에서) */
    public Scenario nextScenario(Game game, int nextTurn) {
        // TODO: spawn 조건(중분류 가중치, operator) 평가 로직으로 교체
        return scenarioRepo.findAll()
                .stream().skip(1).findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.SCENARIO_NOT_FOUND));
    }

}