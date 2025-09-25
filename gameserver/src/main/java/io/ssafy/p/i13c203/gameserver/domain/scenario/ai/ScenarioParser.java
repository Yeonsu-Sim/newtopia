package io.ssafy.p.i13c203.gameserver.domain.scenario.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.ConditionEntryDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.EffectDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.EffectScoresDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.EffectWeightsDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.model.CardType;
import io.ssafy.p.i13c203.gameserver.domain.game.model.ConditionOperator;
import io.ssafy.p.i13c203.gameserver.domain.game.model.MajorCategory;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.ChoiceDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.PressReleaseDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.RelatedArticleDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.SpawnConditionsDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.NewsEvent;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Npc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
import io.ssafy.p.i13c203.gameserver.domain.scenario.model.EffectApplyType;
import io.ssafy.p.i13c203.gameserver.domain.scenario.service.NpcService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScenarioParser {

    private final NpcService npcService;
    private final ObjectMapper objectMapper;
    private static final double WEIGHT_VALUE = 0.2;


    public Scenario parseResponseToScenario(String response, NewsEvent newsEvent) {
        try {
            JsonNode root = objectMapper.readTree(response);

            String title = root.path("title")
                    .asText();
            String content = root.path("content")
                    .asText();

            SpawnConditionsDoc spawn = parseConditions(root.path("conditions"));

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
                    .spawn(spawn)
                    .choices(choices)
                    .relatedArticle(relatedArticleDoc)
                    .type(CardType.ORIGIN)
                    .articleId(articleId)
                    .build();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private SpawnConditionsDoc parseConditions(JsonNode conditionsNode) {
        List<ConditionEntryDoc> list = new ArrayList<>();

        if (conditionsNode != null && conditionsNode.isArray()) {
            for (JsonNode n : conditionsNode) {
                String categoryStr = n.path("category")
                        .asText(null);   // "economy" | "defense" | "environment" | "publicSentiment"
                String operatorStr = n.path("operator")
                        .asText(null);   // "LESS_THAN" | "MORE_THAN"
                int thrInt = n.path("threshold")
                        .asInt(0);

                // threshold 0~100 클램프
                if (thrInt < 0) {
                    thrInt = 0;
                }
                if (thrInt > 100) {
                    thrInt = 100;
                }

                try {
                    MajorCategory category = parseMajorCategorySafe(categoryStr);
                    ConditionOperator operator = ConditionOperator.valueOf(operatorStr); // 대문자 기대

                    list.add(new ConditionEntryDoc(category, operator, (double) thrInt));
                } catch (Exception ex) {
                    log.warn("조건 무시: category={}, operator={}, threshold={}, reason={}",
                            categoryStr, operatorStr, thrInt, ex.toString());
                }
            }
        }

        // 유효 조건이 없으면 null 반환(컬럼 nullable이므로)
        return list.isEmpty() ? null : new SpawnConditionsDoc(list);
    }

    // 대소문자 무시 매핑 (LLM이 'Environment' 등으로 줄 수 있음)
    private MajorCategory parseMajorCategorySafe(String s) {
        if (s == null) {
            throw new IllegalArgumentException("category is null");
        }
        for (MajorCategory mc : MajorCategory.values()) {
            if (mc.name()
                    .equalsIgnoreCase(s)) {
                return mc;
            }
        }
        throw new IllegalArgumentException("unknown category: " + s);
    }

    private Map<String, ChoiceDoc> parseChoices(JsonNode choicesNode) {
        Map<String, ChoiceDoc> choices = new HashMap<>();

        if (choicesNode.isObject()) {
            choicesNode.properties()
                    .forEach(property -> {
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
