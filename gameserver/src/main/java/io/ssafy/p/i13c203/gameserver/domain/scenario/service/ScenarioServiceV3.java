package io.ssafy.p.i13c203.gameserver.domain.scenario.service;

import static io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ssafy.p.i13c203.gameserver.common.ai.OpenAiClient;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.EffectDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.EffectScoresDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.EffectWeightsDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.domain.game.model.CardType;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.ChoiceDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.PressReleaseDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.RelatedArticleDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.NewsEvent;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Npc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
import io.ssafy.p.i13c203.gameserver.domain.scenario.model.EffectApplyType;
import io.ssafy.p.i13c203.gameserver.domain.scenario.repository.NewsEventRepository;
import io.ssafy.p.i13c203.gameserver.domain.scenario.repository.ScenarioRepository;
import io.ssafy.p.i13c203.gameserver.global.exception.NotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioServiceV3 implements ScenarioService {

    private final OpenAiClient openAiClient;
    private final NewsEventRepository newsEventRepository;
    private final ScenarioRepository scenarioRepository;
    private final NpcService npcService;
    private static final String MODEL = "gpt-4.1";
    private final ObjectMapper objectMapper;
    private static final double WEIGHT_VALUE = 0.2;

    @Transactional
    @Override
    public Scenario firstScenario(Long memberId) {
        Pageable pageable = PageRequest.of(0, 1);

        List<Scenario> randomByType = scenarioRepository.findRandomByType(CardType.ORIGIN,
                pageable);

        // 생성된 시나리오가 있을 경우
        if (!randomByType.isEmpty()) {
            return randomByType.get(0);
        }

        // 생성된 시나리오가 없을 경우
        NewsEvent newsEvent = newsEventRepository.findRandom()
                .orElseThrow(() -> new NotFoundException(NEWS_NOT_FOUND));

        String response = openAiClient.chatCompletion(MODEL, getSystemPrompt(),
                getUserPrompt(newsEvent));

        Scenario scenario = parseResponseToScenario(response, newsEvent);
        scenarioRepository.save(scenario);

        return scenario;
    }


    @Override
    public Scenario nextScenario(Game game) {
        // TODO 어떤 조건으로 다음 시나리오를 가져올 것인가?

        return null;
    }

    private String getSystemPrompt() {
        return """
            당신은 정치 시뮬레이션 게임의 시나리오 작가입니다.
            주어진 뉴스를 바탕으로 아래 시나리오 객체 형식에 맞게 JSON을 생성해주세요.
            
            【뉴스 정보】
            제목: %s
            내용: %s
            카테고리: %s
            감정: %s
            
            【시나리오 객체 구조】
            - title: 시나리오 제목 (20자 이내)
            - content: NPC가 플레이어(지도자)에게 직접 말하는 것처럼 구어체로 작성 (50자 이내, 게임다운 톤앤매너)
            - choices: { "A": Choice, "B": Choice }
              - code: "A" 또는 "B"
              - content: 선택지 설명
              - effect:
                - scores: { economy, defense, environment, publicSentiment } (정수 -20~20)
              - comments: 국민 반응 문자열 배열
            
            【점수(밸런스) 규칙】
            1) 각 score는 -20 이상 20 이하의 정수여야 한다.
            2) “선택지 내용 ↔ scores”가 논리적으로 일치해야 한다.
               - 예: 환경 보호 정책 → environment는 +(소폭~중간폭), 규제 강화로 경제 부담 → economy는 -(소폭)
            3) 과도한 급등락 방지(지표는 0~100에서 끝남):
               - 일반 뉴스(보통 강도): 주효과 ±8~±18, 부수 효과 ±3~±10, 상쇄 효과(트레이드오프) ±2~±8
               - 큰 이슈(강한 강도): 주효과 최대 ±25까지 허용하되, 반드시 1개 이상 상쇄 효과 포함
               - 아주 경미한 이슈(약한 강도): 모든 효과 절대값을 3~10 사이로 제한
               (강도는 기사 내용/감정에서 추론하되, 과도한 값 사용 금지)
            4) 네 지표가 모두 같은 방향(전부 + 또는 전부 -)이 되지 않도록 최소 1개 이상의 트레이드오프를 포함한다.
            5) 의미 없는 0 남발 금지: 최소 2개 이상의 지표에 |score| ≥ 3을 부여한다.
            6) A/B는 서로 다른 전략과 결과를 보여야 하며, 동일/유사한 분포를 피한다.
            
            【comments 규칙】
            - 최소 2개 이상, 실제 시민 반응처럼 구체적으로 작성(이해관계·우려·지지 등 혼합 가능)
            - 과도한 비속어 금지, 짧고 명확하게
            
            【요구사항】
            1. 반드시 유효한 JSON만 응답할 것
            2. scores와 weights는 각각 의미 있는 수치를 설정할 것
            3. comments는 최소 2개 이상, 실제 시민 반응처럼 작성할 것
            """;
    }

    private String getUserPrompt(NewsEvent newsEvent) {
        try {
            String categoriesJson = objectMapper.writeValueAsString(newsEvent.getNewsCategoryDoc());
            String sentimentJson = objectMapper.writeValueAsString(newsEvent.getSentimentDoc());

            return """
                    【뉴스 정보】
                    제목: %s
                    내용: %s
                    카테고리: %s
                    감정: %s
                    """.formatted(
                    newsEvent.getTitle(),
                    newsEvent.getContent(),
                    categoriesJson,
                    sentimentJson
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Scenario parseResponseToScenario(String response, NewsEvent newsEvent) {
        try {
            JsonNode root = objectMapper.readTree(response);

            String title = root.path("title")
                    .asText();
            String content = root.path("content")
                    .asText();

            Map<String, ChoiceDoc> choices = parseChoices(root.path("choices"));

            String articleId = newsEvent.getArticleId();
            RelatedArticleDoc relatedArticleDoc = new RelatedArticleDoc(
                    newsEvent.getTitle(),
                    newsEvent.getSourceUrl(),
                    newsEvent.getContent()
            );

            Npc npc = npcService.getNpcByCategoryPlainJava(
                    newsEvent.getNewsCategoryDoc()
                            .majorCategories()
                            .get(0)
                            .category());

            return Scenario.builder()
                    .title(title)
                    .content(content)
                    .npc(npc)
                    .spawn(null)
                    .choices(choices)
                    .relatedArticle(relatedArticleDoc)
                    .type(CardType.ORIGIN)
                    .articleId(articleId)
                    .build();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private Map<String, ChoiceDoc> parseChoices(JsonNode choicesNode) {
        Map<String, ChoiceDoc> choices = new HashMap<>();

        if (choicesNode.isObject()) {
            choicesNode.properties().forEach(property -> {
                String choiceKey = property.getKey();
                JsonNode choiceValue = property.getValue();

                try {
                    ChoiceDoc choice = parseChoice(choiceValue);
                    choices.put(choiceKey, choice);
                } catch (Exception e) {
                    log.warn("선택지 파싱 실패: key={}", choiceKey, e);
                }
            });
        }

        return choices;
    }

    private ChoiceDoc parseChoice(JsonNode choiceNode) {
        String code = choiceNode.path("code")
                .asText();
        String content = choiceNode.path("content")
                .asText();

        // effect 파싱
        EffectDoc effect = parseEffect(choiceNode.path("effect"));

        // comments 파싱
        List<String> comments = new ArrayList<>();
        JsonNode commentsNode = choiceNode.path("comments");
        if (commentsNode.isArray()) {
            commentsNode.forEach(comment -> comments.add(comment.asText()));
        }

        PressReleaseDoc pressReleaseDoc = new PressReleaseDoc("", "", "");

        return new ChoiceDoc(code, content, effect, pressReleaseDoc, comments);
    }

    private EffectDoc parseEffect(JsonNode effectNode) {
        // scores 파싱
        JsonNode scoresNode = effectNode.path("scores");
        EffectScoresDoc scores = new EffectScoresDoc(
                EffectApplyType.RELATIVE,
                scoresNode.path("economy")
                        .asInt(),
                scoresNode.path("defense")
                        .asInt(),
                scoresNode.path("publicSentiment")
                        .asInt(),
                scoresNode.path("environment")
                        .asInt()
        );

        EffectWeightsDoc weights = new EffectWeightsDoc(
                EffectApplyType.RELATIVE,
                WEIGHT_VALUE,
                WEIGHT_VALUE,
                WEIGHT_VALUE,
                WEIGHT_VALUE,
                WEIGHT_VALUE,
                WEIGHT_VALUE,
                WEIGHT_VALUE,
                WEIGHT_VALUE,
                WEIGHT_VALUE,
                WEIGHT_VALUE,
                WEIGHT_VALUE,
                WEIGHT_VALUE,
                WEIGHT_VALUE,
                WEIGHT_VALUE,
                WEIGHT_VALUE,
                WEIGHT_VALUE
        );

        return new EffectDoc(scores, weights);
    }
}
