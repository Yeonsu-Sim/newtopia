package io.ssafy.p.i13c203.gameserver.domain.game.service;

import io.ssafy.p.i13c203.gameserver.domain.game.doc.CardDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
import io.ssafy.p.i13c203.gameserver.domain.scenario.service.EventScenarioService;
import io.ssafy.p.i13c203.gameserver.domain.scenario.service.ScenarioService;
import io.ssafy.p.i13c203.gameserver.domain.scenario.service.ScenarioServiceForPresentation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardGeneratorImplForPresentation implements CardGenerator {

    private final EventScenarioService eventScenarioService;
    private final ScenarioService scenarioService;
    private final EventIntervalService eventIntervalService;

    // TODO: HARD CODING for Presentation
    private final ScenarioServiceForPresentation scenarioServiceForPresentation;

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


        // TODO: HARD CODING for Presentation
        // 첫번째 시나리오
        if(memberId == 2 && currentTurn == 1){
            Scenario sc = scenarioServiceForPresentation.getFirst();

            return CardDoc.from(sc);
        }

        // 두번째 시나리오
        if(memberId == 2 && currentTurn == 2){
            Scenario sc = scenarioServiceForPresentation.getSecond();
            return CardDoc.from(sc);
        }



        // 시연용 3.  memberId 라면 무조건 하드코딩된 이벤트로 가야함
        if(memberId == 2 && currentTurn == 3){
            // 이벤트 시나리오임
            var sc = eventScenarioService.nextScenarioForPresentation();

            eventIntervalService.updateAfterTrigger(game.getId(), currentTurn);

            return CardDoc.from(sc);
        }

        // ----------------------------------------------------------------------

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
