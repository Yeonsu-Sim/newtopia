package io.ssafy.p.i13c203.gameserver.domain.scenario.service;

import static io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode.*;

import io.ssafy.p.i13c203.gameserver.common.ai.OpenAiClient;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.domain.game.model.CardType;
import io.ssafy.p.i13c203.gameserver.domain.game.model.CountryStats;
import io.ssafy.p.i13c203.gameserver.domain.game.repository.GameHistoryRepository;
import io.ssafy.p.i13c203.gameserver.domain.game.repository.GameRepository;
import io.ssafy.p.i13c203.gameserver.domain.scenario.ai.PromptFactory;
import io.ssafy.p.i13c203.gameserver.domain.scenario.ai.ScenarioParser;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.NewsEvent;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
import io.ssafy.p.i13c203.gameserver.domain.scenario.repository.NewsEventRepository;
import io.ssafy.p.i13c203.gameserver.domain.scenario.repository.ScenarioRepository;
import io.ssafy.p.i13c203.gameserver.global.exception.NotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioServiceV3 implements ScenarioService {

    private final OpenAiClient openAiClient;
    private final ScenarioParser scenarioParser;
    private final PromptFactory promptFactory;
    private final NewsEventRepository newsEventRepository;
    private final ScenarioRepository scenarioRepository;
    private final GameRepository gameRepository;
    private final GameHistoryRepository gameHistoryRepository;
    private static final String MODEL = "gpt-4.1";

    @Transactional
    @Override
    public Scenario firstScenario(Long memberId) {
        // 멤버가 진행했던 게임 조회
        List<Long> seenScenarioIds = loadSeenScenarioIds(memberId);

        // ORIGIN 카드 중, 본 것 제외 + 랜덤 1개
        Pageable one = PageRequest.of(0, 1);
        List<Scenario> candidates = scenarioRepository.findRandomByTypeExcluding(
                CardType.ORIGIN, seenScenarioIds, one
        );

        if (!candidates.isEmpty()) {
            return candidates.get(0);
        }

        return generateFromNews();
    }


    @Transactional(readOnly = true)
    @Override
    public Scenario nextScenario(Game game) {
        List<Long> seenScenarioIds = loadSeenScenarioIds(game.getMemberId());
        CountryStats st = game.getCountryStats();

        // 조건에 충족하는 시나리오 조회
        return scenarioRepository.findOneEligibleRandomExcluding(
                        CardType.ORIGIN.name(), st.getEconomy(), st.getDefense(), st.getEnvironment(),
                        st.getPublicSentiment(), seenScenarioIds
                )
                .orElseGet(this::generateFromNews);
    }


    // 사용자가 봤던 시나리오 조회
    private List<Long> loadSeenScenarioIds(Long memberId) {
        List<Long> gameIds = gameRepository.findGameIdsByMemberId(memberId);
        if (gameIds.isEmpty()) {
            return List.of();
        }
        return gameHistoryRepository.findDistinctScenarioIdsByGameIds(gameIds);
    }

    // 새로운 시나리오 생성
    private Scenario generateFromNews() {
        NewsEvent newsEvent = newsEventRepository.findRandom()
                .orElseThrow(() -> new NotFoundException(NEWS_NOT_FOUND));

        String systemPrompt = promptFactory.getSystemPrompt();
        String userPrompt = promptFactory.getUserPrompt(newsEvent);

        String response = openAiClient.chatCompletion(MODEL, systemPrompt, userPrompt);
        Scenario scenario = scenarioParser.parseResponseToScenario(response, newsEvent);
        return scenarioRepository.save(scenario);
    }

}
