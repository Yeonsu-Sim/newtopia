package io.ssafy.p.i13c203.gameserver.domain.game.service;

import io.ssafy.p.i13c203.gameserver.domain.game.doc.CardDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.domain.scenario.service.EventScenarioService;
import io.ssafy.p.i13c203.gameserver.domain.scenario.service.ScenarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardGeneratorImpl implements CardGenerator {

    private final EventScenarioService eventScenarioService;
    private final ScenarioService scenarioService;
    private final EventIntervalService eventIntervalService;


    /**
     * Scenario → CardDoc 변환 로직 (첫 카드)
     */
    @Override
    public CardDoc createFirstCard(Long memberId) {
        // 첫 시나리오 선택은 ScenarioService가 담당
        var sc = scenarioService.firstScenario(memberId);
        return CardDoc.from(sc);
    }

    /**
     * Scenario → CardDoc 변환 로직 (다음 카드)
     */
    @Override
    public CardDoc createNextCard(Long memberId, Game game) {

        int currentTurn = game.getTurn();
        // Redis에 저장된 상태로 이벤트 발생 여부 판단
        if (eventIntervalService.shouldTrigger(game.getId(), currentTurn)) {
            var sc = eventScenarioService.nextScenario();

            // 이벤트가 발생했으니 인터벌 갱신
            eventIntervalService.updateAfterTrigger(game.getId(), currentTurn);

            return CardDoc.from(sc);
        }

        // 일반 시나리오
        var sc = scenarioService.nextScenario(game);
        return CardDoc.from(sc);
    }
}
