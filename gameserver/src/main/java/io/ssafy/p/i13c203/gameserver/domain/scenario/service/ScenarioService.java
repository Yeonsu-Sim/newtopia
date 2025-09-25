package io.ssafy.p.i13c203.gameserver.domain.scenario.service;

import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
import io.ssafy.p.i13c203.gameserver.domain.scenario.repository.ScenarioRepository;
import io.ssafy.p.i13c203.gameserver.global.exception.BusinessException;
import io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;


public interface ScenarioService {



    /** 게임 시작 시 선택할 첫 시나리오를 반환 (랜덤) */
    public Scenario firstScenario(Long memberId);

    /**
     * 다음 턴에 사용할 시나리오를 반환 (랜덤)
     * 추후 등장 조건 평가는 이 메서드에서 진행
     */
    public Scenario nextScenario(Game game) ;

}