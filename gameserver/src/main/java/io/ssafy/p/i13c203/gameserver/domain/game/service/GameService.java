package io.ssafy.p.i13c203.gameserver.domain.game.service;

import io.ssafy.p.i13c203.gameserver.domain.game.doc.*;
import io.ssafy.p.i13c203.gameserver.domain.game.model.CardType;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.ChoiceDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.GameHistory;
import io.ssafy.p.i13c203.gameserver.domain.game.idem.annotation.IdempotentOperation;
import io.ssafy.p.i13c203.gameserver.domain.game.model.ChoiceWeights;
import io.ssafy.p.i13c203.gameserver.domain.game.model.CountryStats;
import io.ssafy.p.i13c203.gameserver.domain.game.repository.GameHistoryRepository;
import io.ssafy.p.i13c203.gameserver.domain.game.repository.GameRepository;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.NpcRefDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.SpawnConditionsDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Npc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
import io.ssafy.p.i13c203.gameserver.domain.scenario.repository.NpcRepository;
import io.ssafy.p.i13c203.gameserver.domain.scenario.service.ScenarioService;
import io.ssafy.p.i13c203.gameserver.global.exception.BusinessException;
import io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepo;
    private final GameHistoryRepository historyRepo;
    private final NpcRepository npcRepo;
    private final ScenarioService scenarioService;

    @Transactional(readOnly = true)
    public Game findByIdOrThrow(Long gameId) {
        return gameRepo.findById(gameId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Game findByIdOrThrow(Long gameId, Long memberId) {
        Game game = findByIdOrThrow(gameId);
        if (!java.util.Objects.equals(game.getMemberId(), memberId)) {
            throw new BusinessException(ErrorCode.NOT_VALID, "본인의 게임만 조회할 수 있습니다.");
        }
        return game;
    }

    // ========== 9.1 진행중인 게임 조회 ==========
    @Transactional(readOnly = true)
    public Optional<Game> getActiveGame(Long memberId) {
        return gameRepo.findFirstByMemberIdAndActiveTrueOrderByCreatedAtDesc(memberId);
    }


    // ========== 9.2 게임 생성 ==========
    @Transactional
    public Game createGame(Long memberId, String countryName, boolean force) {
        Optional<Game> existing = gameRepo.findFirstByMemberIdAndActiveTrueOrderByCreatedAtDesc(memberId);
        if (existing.isPresent() && !force) {
            throw new BusinessException(ErrorCode.GAME_ALREADY_ACTIVE);
        }
        existing.ifPresent(g -> { g.setActive(false); }); // force=true면 종료 표기


        CountryStats stats = CountryStats.builder().build();
        ChoiceWeights weights = ChoiceWeights.builder().build(); // 기본 0

        // 1) 첫 시나리오 선택은 ScenarioService가 담당
        // TODO: 임시 테스트 데이터 수거
        Scenario sc = makeTestScenario();
//        Scenario sc = scenarioService.firstScenario();

        // 2) CardDoc 생성은 GameService가 담당
        CardDoc first = toCardDoc(sc, CardType.ORIGIN);

        Game game = Game.builder()
                .memberId(memberId)
                .countryName(countryName)
                .countryStats(stats)
                .choiceWeights(weights)
                .currentCard(first)
                .currentChoices(first.choices())
                .active(true)
                .build();

        return gameRepo.saveAndFlush(game);
    }

    // ========== 9.3 선택 반영 (AOP 멱등성) ==========
    @IdempotentOperation(
            hashArgs = {"gameId","cardId","choiceCode"},
            ttlSeconds = 600,
            lockSeconds = 10,
            waitMillis = 800,
            spinIntervalMillis = 40)
    @Transactional
    public SubmitChoiceResult submitChoice(Long gameId, Long memberId, UUID cardId, String choiceCode) {
        Game game = findByIdOrThrow(gameId, memberId);
        if (!game.isActive()) throw new BusinessException(ErrorCode.GAME_CLOSED);
        if (game.getCurrentCard() == null || !Objects.equals(game.getCurrentCard().cardId(), cardId)) {
            throw new BusinessException(ErrorCode.STALE_CARD);
        }
        ChoiceDoc chosen = game.getCurrentChoices().get(choiceCode);
        if (chosen == null) throw new BusinessException(ErrorCode.INVALID_CHOICE_CODE);


        // 1) choice code -> effect
        EffectDoc effect = chosen.effect();


        // 2) effect.weights를 choiceWeights에 가산 (0~1 클램프)
        game.setChoiceWeights( addWeights(game.getChoiceWeights(), effect.weights()) );


        // 3) 최종 영향 점수 = effect.scores × (대분류별 중분류 가중치 합) → 반올림 → countryStats에 가산(**0~100** 클램프)
        int dEco = (int) Math.round(effect.scores().economy() * game.getChoiceWeights().sumEconomy());
        int dDef = (int) Math.round(effect.scores().defense() * game.getChoiceWeights().sumDefense());
        int dOpi = (int) Math.round(effect.scores().publicSentiment() * game.getChoiceWeights().sumPublicSentiment());
        int dEnv = (int) Math.round(effect.scores().environment() * game.getChoiceWeights().sumEnvironment());
        game.getCountryStats().addDelta(dEco, dDef, dOpi, dEnv);


        // 4) 히스토리 스냅샷 저장 (별도 테이블)
        HistoryEntryDoc entry = new HistoryEntryDoc(
                game.getTurn(),
                choiceCode,
                new CountryStatsDoc(
                        game.getCountryStats().getEconomy(),
                        game.getCountryStats().getDefense(),
                        game.getCountryStats().getPublicSentiment(),
                        game.getCountryStats().getEnvironment()
                ),
                toWeightsDoc(game.getChoiceWeights()),
                game.getCurrentCard(),
                Instant.now()
        );
        historyRepo.save(GameHistory.builder()
                .gameId(gameId)
                .turn(game.getTurn())
                .entry(entry)
                .build());


        // 5) 다음 카드 선정 (시나리오 서비스: 파라미터 임시로 game 전체)
        int nextTurn = game.getTurn() + 1;

        // 1) 다음 시나리오 고르기 (조건평가 포함)
        // TODO: 임시 테스트 데이터 수거
        Scenario nextScenario = makeTestScenario();
//        Scenario nextScenario = scenarioService.nextScenario(game, nextTurn);

        // 2) 시나리오 → CardDoc
        CardDoc next = toCardDoc(nextScenario, CardType.CONSEQUENCE);

        game.setTurn(nextTurn);
        game.setCurrentCard(next);
        game.setCurrentChoices(next.choices());
        game.setChoosedCode(choiceCode);


        try { gameRepo.saveAndFlush(game); }
        catch (OptimisticLockingFailureException e) { throw new BusinessException(ErrorCode.CONCURRENCY_CONFLICT); }


        return SubmitChoiceResult.from(game, entry, next);
    }

    private ChoiceWeights addWeights(ChoiceWeights base, EffectWeightsDoc d) {
        ChoiceWeights delta = ChoiceWeights.builder()
                .macroeconomy(d.macroeconomy()).fiscalPolicy(d.fiscalPolicy()).financialMarkets(d.financialMarkets()).industryBusiness(d.industryBusiness())
                .militarySecurity(d.militarySecurity()).alliances(d.alliances()).cyberSpace(d.cyberSpace()).publicSafety(d.publicSafety())
                .publicOpinion(d.publicOpinion()).socialIssues(d.socialIssues()).protestsStrikes(d.protestsStrikes()).healthWelfare(d.healthWelfare())
                .climateChangeEnergy(d.climateChangeEnergy()).pollutionDisaster(d.pollutionDisaster()).biodiversity(d.biodiversity()).resourceManagement(d.resourceManagement())
                .build();
        base.add(delta);
        return base;
    }


    private static ChoiceWeightsDoc toWeightsDoc(ChoiceWeights w) {
        return new ChoiceWeightsDoc(
                w.getMacroeconomy(), w.getFiscalPolicy(), w.getFinancialMarkets(), w.getIndustryBusiness(),
                w.getMilitarySecurity(), w.getAlliances(), w.getCyberSpace(), w.getPublicSafety(),
                w.getPublicOpinion(), w.getSocialIssues(), w.getProtestsStrikes(), w.getHealthWelfare(),
                w.getClimateChangeEnergy(), w.getPollutionDisaster(), w.getBiodiversity(), w.getResourceManagement()
        );
    }

    private CardDoc toCardDoc(Scenario sc, CardType type) {
        Npc npc = sc.getNpc();
        if (npc == null) throw new BusinessException(ErrorCode.NPC_NOT_FOUND);

        NpcRefDoc npcRef = new NpcRefDoc(
                npc.getId(),           // Long (PK)
                npc.getName(),
                npc.getImageS3Key()
        );

        return new CardDoc(
                java.util.UUID.randomUUID(), // cardId: 런타임 UUID
                sc.getId(),                  // scenarioId: Long
                type,
                sc.getTitle(),
                sc.getContent(),
                npcRef,
                sc.getSpawn(),
                sc.getChoices(),
                sc.getRelatedArticle()
        );
    }

    /**
     * 임시 테스트 시나리오 반환
     */
    private Scenario makeTestScenario() {
        // 1) 첫 시나리오 선택은 ScenarioService가 담당
        Scenario sc = new Scenario();

        // ----- 기본 메타 -----
        sc.setId(1L);
        sc.setTitle("새 정부의 경제 어젠다");
        sc.setContent("새로운 내각이 경제정책 우선순위를 논의합니다.");

        // ----- NPC (참조만 설정: id, name, imageS3Key) -----
        Npc npc = new Npc();
        npc.setId(101L);
        npc.setName("대변인");
        npc.setImageS3Key("npc/spokesperson.png");
        sc.setNpc(npc);

        // ----- 스폰 조건 (첫 카드: 조건 없음) -----
        sc.setSpawn(new SpawnConditionsDoc(
                        java.util.List.of() // 빈 조건 목록
                )
        );

        // ----- 선택지(effect: scores + weights) -----
        final String[] MINORS = {
                "macroeconomy","fiscalPolicy","financialMarkets","industryBusiness",
                "militarySecurity","alliances","cyberSpace","publicSafety",
                "publicOpinion","socialIssues","protestsStrikes","healthWelfare",
                "climateChangeEnergy","pollutionDisaster","biodiversity","resourceManagement"
        };

        // 16개 중분류 가중치
        EffectWeightsDoc fullWeights = new  EffectWeightsDoc(
                0.1, 0.2, 0.1, 0.1,
                0.1, 0.2, 0.1, 0.1,
                0.1, 0.2, 0.1, 0.1,
                0.1, 0.2, 0.1, 0.1
                );

        // scores 도메인 객체(대분류 4개)
        EffectScoresDoc scoresA =
                new EffectScoresDoc(3, 0, 1, 0); // economy, defense, publicSentiment, environment
        EffectScoresDoc scoresB =
                new EffectScoresDoc(1, 0, 0, 0);

        // effect
        EffectDoc effectA =
                new EffectDoc(scoresA, fullWeights);
        EffectDoc effectB =
                new EffectDoc(scoresB, fullWeights);

        // choice A/B
        ChoiceDoc choiceA =
                new ChoiceDoc("A", "일자리 창출 계획 가속", effectA);
        ChoiceDoc choiceB =
                new ChoiceDoc("B", "재정 건전성 최우선", effectB);

        // 선택지 맵(순서 고정용 LinkedHashMap)
        java.util.Map<String, io.ssafy.p.i13c203.gameserver.domain.scenario.doc.ChoiceDoc> choices =
                new java.util.LinkedHashMap<>();
        choices.put("A", choiceA);
        choices.put("B", choiceB);
        sc.setChoices(choices);

        // ----- 관련 기사 -----
        sc.setRelatedArticle(
                new io.ssafy.p.i13c203.gameserver.domain.scenario.doc.RelatedArticleDoc(
                        "경제 컨퍼런스 개최",
                        "https://news.example/econ-1"
                )
        );
        return sc;
    }
}


