package io.ssafy.p.i13c203.gameserver.domain.game.service;

import io.ssafy.p.i13c203.gameserver.domain.ending.entity.Ending;
import io.ssafy.p.i13c203.gameserver.domain.ending.service.EndingService;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.*;
import io.ssafy.p.i13c203.gameserver.domain.game.dto.ChoiceHintDTO;
import io.ssafy.p.i13c203.gameserver.domain.game.dto.ChoiceHintDTO.Choice;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.doc.AppliedDoc;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.service.GameResultService;
import io.ssafy.p.i13c203.gameserver.domain.ranking.service.RankingService;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.ChoiceDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.GameHistory;
import io.ssafy.p.i13c203.gameserver.domain.game.idem.annotation.IdempotentOperation;
import io.ssafy.p.i13c203.gameserver.domain.game.model.ChoiceWeights;
import io.ssafy.p.i13c203.gameserver.domain.game.model.CountryStats;
import io.ssafy.p.i13c203.gameserver.domain.game.repository.GameHistoryRepository;
import io.ssafy.p.i13c203.gameserver.domain.game.repository.GameRepository;
import io.ssafy.p.i13c203.gameserver.domain.scenario.model.EffectApplyType;
import io.ssafy.p.i13c203.gameserver.global.exception.BusinessException;
import io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode;
import io.ssafy.p.i13c203.gameserver.global.exception.NotFoundException;
import java.time.Instant;
import java.util.Map;
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
    private final EndingService endingService;
    private final RankingService rankingService;
    private final GameResultService gameResultService;
    private final CardGenerator cardFactory;
    private final EventIntervalService eventIntervalService;

    private static final int DIFFICULTY_INTERVAL = 3;
    private static final int SMALL_MAX = 10;
    private static final int MEDIUM_MAX = 20;

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
        Optional<Game> existing = getActiveGame(memberId);
        if (existing.isPresent() && !force) {
            throw new BusinessException(ErrorCode.GAME_ALREADY_ACTIVE);
        }

        // force=true면 종료 표기
        existing.ifPresent(g -> {
            g.setActive(false);
            eventIntervalService.clear(g.getId());
        });

        CountryStats stats = CountryStats.builder()
                .build();
        ChoiceWeights weights = ChoiceWeights.builder()
                .build(); // 기본 0

        // 첫 질문 카드 생성
        CardDoc first = cardFactory.createFirstCard(memberId);

        Game game = Game.builder()
                .memberId(memberId)
                .countryName(countryName)
                .countryStats(stats)
                .choiceWeights(weights)
                .currentCard(first)
                .currentChoices(first.choices())
                .active(true)
                .turn(1)
                .build();

        Game savedGame = gameRepo.saveAndFlush(game);

        // --------  히스토리 저장  ----------
        HistoryEntryDoc entry = new HistoryEntryDoc(
                0,
                null,
                CountryStatsDoc.from(stats),
                ChoiceWeightsDoc.from(game.getChoiceWeights()),
                null,
                Instant.now(),
                null,
                null
        );

        historyRepo.save(GameHistory.builder()
                .gameId(savedGame.getId())
                .entry(entry)
                .build());

        return savedGame;
    }

    // ========== 9.3 선택 반영 (AOP 멱등성) ==========

    @IdempotentOperation(
            hashArgs = {"gameId", "cardId", "choiceCode"},
            ttlSeconds = 600, lockSeconds = 10, waitMillis = 800, spinIntervalMillis = 40)
    @Transactional
    public SubmitChoiceResult submitChoice(Long gameId, Long memberId, UUID cardId,
            String choiceCode) {
        Game game = findByIdOrThrow(gameId, memberId);
        if (!game.isActive()) {
            throw new BusinessException(ErrorCode.GAME_CLOSED);
        }
        if (game.getCurrentCard() == null || !Objects.equals(game.getCurrentCard()
                .cardId(), cardId)) {
            throw new BusinessException(ErrorCode.STALE_CARD);
        }
        ChoiceDoc chosen = game.getCurrentChoices()
                .get(choiceCode);
        if (chosen == null) {
            throw new BusinessException(ErrorCode.INVALID_CHOICE_CODE);
        }

        // -------- (0) 적용 "이전" 스냅샷 ----------
        CountryStatsDoc beforeStats = CountryStatsDoc.from(game.getCountryStats());
        int finishedTurn = game.getTurn();

        // -------- (1) effect 반영 ----------
        EffectDoc effect = chosen.effect();
        applyWeights(game, effect.weights());
        applyScores(game, effect.scores());

        // 적용 "이후" 스냅샷
        CountryStatsDoc afterStats = CountryStatsDoc.from(game.getCountryStats());

        // -------- (2) 히스토리 저장 (after 기준 스냅샷) ----------
        HistoryEntryDoc entry = new HistoryEntryDoc(
                finishedTurn,
                choiceCode,
                afterStats,
                ChoiceWeightsDoc.from(game.getChoiceWeights()),
                game.getCurrentCard(),
                Instant.now(),
                AppliedDoc.of(beforeStats, afterStats),
                game.getCurrentCard()
                        .choices()
                        .get(choiceCode)
                        .label()
        );
        historyRepo.save(GameHistory.builder()
                .gameId(gameId)
                .turn(finishedTurn)
                .entry(entry)
                .build());

        // -------- (3) 게임 종료 판단 ----------
        Ending ending = endingService.getEndingOrNull(game);
        game.setChoosedCode(choiceCode); // 사용자가 고른 코드 기록

        if (ending != null) {
            game.markEnded(ending); // endingCode, endedAt, active=false

            try {
                gameRepo.saveAndFlush(game);
            } catch (OptimisticLockingFailureException e) {
                throw new BusinessException(ErrorCode.CONCURRENCY_CONFLICT);
            }

            // 이벤트 발생 간격 레디스에서 수거
            eventIntervalService.clear(gameId);

            // 게임 결과 생성
            gameResultService.createOnEnding(memberId, game);

            // TODO: Check Ranking Service 입력
            rankingService.registerRanking(game);

            // 엔딩 응답: nextCard=null, 다음 턴 증가 없음
            return SubmitChoiceResult.ended(
                    finishedTurn, choiceCode, beforeStats, afterStats, ending
            );
        }

        // -------- (4) 다음 카드 선정 ----------
        int nextTurn = finishedTurn + 1;
        game.setTurn(nextTurn);

        CardDoc next = cardFactory.createNextCard(memberId, game);
        game.setCurrentCard(next);
        game.setCurrentChoices(next.choices());

        try {
            gameRepo.saveAndFlush(game);
        } catch (OptimisticLockingFailureException e) {
            throw new BusinessException(ErrorCode.CONCURRENCY_CONFLICT);
        }

        return SubmitChoiceResult.progress(
                finishedTurn, choiceCode,
                beforeStats,
                nextTurn, afterStats, next
        );
    }

    @Transactional(readOnly = true)
    public ChoiceHintDTO getHintsAboutChoice(Long gameId) {
        Game findGame = gameRepo.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

//        Map<String, ChoiceDoc> currentChoices = findGame.getCurrentChoices();
        EffectScoresDoc AChoice = findGame.getCurrentChoices()
                .get("A")
                .effect()
                .scores();

        EffectScoresDoc BChoice = findGame.getCurrentChoices()
                .get("B")
                .effect()
                .scores();

        // 현재 게임의 파라미터 점수
        CountryStats countryStats = findGame.getCountryStats();
        Choice AHint = calcScoreChange(AChoice, countryStats);
        Choice BHint = calcScoreChange(BChoice, countryStats);

        return new ChoiceHintDTO(AHint, BHint);
    }

    private Choice calcScoreChange(EffectScoresDoc choice, CountryStats countryStats) {
        int curEconomy = countryStats.getEconomy();
        int curDefense = countryStats.getDefense();
        int curEnvironment = countryStats.getEnvironment();
        int curPublicSentiment = countryStats.getPublicSentiment();

        String eco = toLevelStr(magnitude(choice.applyType(), curEconomy, choice.economy()));
        String def = toLevelStr(magnitude(choice.applyType(), curDefense, choice.defense()));
        String env = toLevelStr(magnitude(choice.applyType(), curEnvironment, choice.environment()));
        String pub = toLevelStr(magnitude(choice.applyType(), curPublicSentiment, choice.publicSentiment()));

        return new Choice(eco, def, env, pub);
    }

    private String toLevelStr(int mag) {
        if (mag == 0)                 return "none";
        if (mag <= SMALL_MAX)         return "small";
        if (mag <= MEDIUM_MAX)        return "medium";
        return "large";
    }

    private int magnitude(EffectApplyType type, int current, int targetOrDelta) {
        return switch (type) {
            case ABSOLUTE -> Math.abs(current - targetOrDelta);
            case RELATIVE -> Math.abs(targetOrDelta);
        };
    }

    /***** UTILS *****/

    private void applyScores(Game game, EffectScoresDoc score) {
        switch (game.getCurrentCard()
                .type()) {
            case EVENT -> {
                switch (score.applyType()) {
                    case RELATIVE -> applyRelative(game, score);
                    case ABSOLUTE -> applyAbsolute(game, score);
                }
            }
            case ORIGIN, CONSEQUENCE -> {
                switch (score.applyType()) {
                    case RELATIVE -> applyRelativeWithWeights(game, score);
                    case ABSOLUTE -> applyAbsolute(game, score);
                }
            }
        }
    }

    private void applyWeights(Game game, EffectWeightsDoc weights) {
        // ***** 난이도 조정 -> 3턴 마다 가중치 합 증가 ***** //
        if (game.getTurn() % DIFFICULTY_INTERVAL == 1) {
            game.setChoiceWeights(addWeights(game.getChoiceWeights(), weights));
        }
    }


    /***** HELPER *****/

    private void applyRelative(Game game, EffectScoresDoc score) {
        int dEco = score.economy();
        int dDef = score.defense();
        int dOpi = score.publicSentiment();
        int dEnv = score.environment();
        game.getCountryStats()
                .addDelta(dEco, dDef, dOpi, dEnv);
    }

    private void applyRelativeWithWeights(Game game, EffectScoresDoc score) {
        int dEco = (int) Math.round(score.economy() * game.getChoiceWeights()
                .sumEconomy());
        int dDef = (int) Math.round(score.defense() * game.getChoiceWeights()
                .sumDefense());
        int dOpi = (int) Math.round(score.publicSentiment() * game.getChoiceWeights()
                .sumPublicSentiment());
        int dEnv = (int) Math.round(score.environment() * game.getChoiceWeights()
                .sumEnvironment());
        game.getCountryStats()
                .addDelta(dEco, dDef, dOpi, dEnv);
    }

    private void applyAbsolute(Game game, EffectScoresDoc score) {
        int aEco = score.economy();
        int aDef = score.defense();
        int aOpi = score.publicSentiment();
        int aEnv = score.environment();
        game.getCountryStats()
                .setValue(aEco, aDef, aOpi, aEnv);
    }

    private ChoiceWeights addWeights(ChoiceWeights base, EffectWeightsDoc d) {
        ChoiceWeights delta = ChoiceWeights.builder()
                .macroeconomy(d.macroeconomy())
                .fiscalPolicy(d.fiscalPolicy())
                .financialMarkets(d.financialMarkets())
                .industryBusiness(d.industryBusiness())
                .militarySecurity(d.militarySecurity())
                .alliances(d.alliances())
                .cyberSpace(d.cyberSpace())
                .publicSafety(d.publicSafety())
                .publicOpinion(d.publicOpinion())
                .socialIssues(d.socialIssues())
                .protestsStrikes(d.protestsStrikes())
                .healthWelfare(d.healthWelfare())
                .climateChangeEnergy(d.climateChangeEnergy())
                .pollutionDisaster(d.pollutionDisaster())
                .biodiversity(d.biodiversity())
                .resourceManagement(d.resourceManagement())
                .build();
        base.add(delta);
        return base;
    }

}


