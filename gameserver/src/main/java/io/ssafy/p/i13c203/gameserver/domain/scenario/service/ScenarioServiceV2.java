package io.ssafy.p.i13c203.gameserver.domain.scenario.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.minio.MinioClient;
import io.minio.SelectObjectContentArgs;
import io.minio.SelectResponseStream;
import io.minio.messages.InputSerialization;
import io.minio.messages.OutputSerialization;
import io.minio.messages.CompressionType;
import io.minio.messages.JsonType;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.domain.game.model.CountryStats;
import io.ssafy.p.i13c203.gameserver.domain.image.entity.Image;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Npc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.repository.ScenarioRepository;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.ChoiceDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.PressReleaseDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.SpawnConditionsDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.RelatedArticleDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.EffectDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.EffectScoresDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.EffectWeightsDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.ConditionEntryDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.model.ConditionOperator;
import io.ssafy.p.i13c203.gameserver.domain.game.model.MinorCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioServiceV2 implements ScenarioService{

    private final SimpleNewsService simpleNewsService;
    private final GenerateScenarioService generateScenarioService;
    private final ObjectMapper objectMapper;
    private final NpcService npcService;




    @Override
    public Scenario firstScenario() {
        JsonNode newsData = simpleNewsService.getRandomNews();
        return createScenarioFromNews(newsData);
    }




    // 속도가 안나서 랜덤으로 하고
    // 후에 db 로 바꾸면 로직 바꿀게요
    @Override
    public Scenario nextScenario(Game game, int nextTurn) {

        JsonNode newsData = simpleNewsService.getRandomNews();
        return createScenarioFromNews(newsData);
//        try {
//            // 게임 상태에 따라 적절한 뉴스 조회
//            JsonNode newsData = simpleNewsService.getNewsForGameState(game);
//
//            if (newsData != null) {
//                log.info("뉴스 조회 성공: {}", newsData.get("title").asText());
//
//                // TODO: JsonNode(뉴스 데이터)를 기반으로 Scenario 객체 생성
//                // 1. 뉴스 제목, 내용을 시나리오 카드로 변환
//                // 2. 선택지 생성 (뉴스 카테고리에 맞는 정책 선택지)
//                // 3. NPC 설정
//
//                return createScenarioFromNews(newsData);
//            } else {
//                log.warn("적절한 뉴스를 찾지 못했습니다. 기본 시나리오를 반환합니다.");
//                return null;
//            }
//
//        } catch (Exception e) {
//            log.error("시나리오 생성 중 오류 발생", e);
//            return null;
//        }
    }

    /**
     * 뉴스 데이터를 기반으로 시나리오 생성
     */
    private Scenario createScenarioFromNews(JsonNode newsData) {
        log.info("뉴스 기반 시나리오 생성: {}", newsData.get("title").asText());

        String gptResponse = generateScenarioService.processNewsWithGPT(newsData);

        if (gptResponse == null || gptResponse.isEmpty()) {
            log.warn("GPT API 응답이 null 또는 빈 값입니다.");
            return null;
        }

        try {
            return parseGptResponseToScenario(gptResponse, newsData);
        } catch (Exception e) {
            log.error("GPT 응답을 Scenario 객체로 변환 중 오류 발생", e);
            return null;
        }
    }

//    /**
//     * 기본 시나리오 생성 (뉴스를 찾지 못한 경우)
//     */
//    private Scenario createDefaultScenario(Game game, int turn) {
//        log.info("기본 시나리오 생성 - 턴: {}", turn);
//
//        // 기본 뉴스 데이터 구조 생성
//        JsonNode defaultNewsData = createDefaultNewsData(turn);
//
//        return generateScenarioService.createScenarioFromNews(defaultNewsData, game);
//    }

//    /**
//     * 기본 뉴스 데이터 생성
//     */
//    private JsonNode createDefaultNewsData(int turn) {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//
//            // 턴별 기본 시나리오 주제
//            String[] defaultTitles = {
//                "경제 정책 변화로 인한 시장 동향",
//                "국방 예산 증액에 대한 국민 여론",
//                "환경 보호 정책 강화 방안",
//                "사회 복지 제도 개선 논의",
//                "국제 관계 개선을 위한 외교적 노력"
//            };
//
//            String[] defaultContents = {
//                "최근 정부의 경제 정책 변화가 시장에 미치는 영향에 대해 각계각층의 의견이 분분합니다.",
//                "국방 예산 증액에 대한 논의가 활발해지고 있으며, 국민들의 다양한 의견이 제시되고 있습니다.",
//                "환경 보호를 위한 새로운 정책 방안이 제시되어 관련 업계의 관심이 집중되고 있습니다.",
//                "사회 복지 제도의 개선 방안에 대한 논의가 진행되고 있으며, 다양한 관점이 제시되고 있습니다.",
//                "국제 사회와의 관계 개선을 위한 외교적 노력이 계속되고 있어 주목받고 있습니다."
//            };
//
//            String[] categories = {"economy", "defense", "environment", "publicSentiment", "defense"};
//
//            int index = turn % defaultTitles.length;
//
//            String defaultNewsJson = String.format("""
//                {
//                  "source_url": "https://default.news.example.com/article/%d",
//                  "title": "%s",
//                  "content": "%s",
//                  "published_at": "2025.09.16. 오전 %d:00",
//                  "categories": {
//                    "major_categories": [
//                      {
//                        "category": "%s",
//                        "confidence": 0.8
//                      }
//                    ]
//                  },
//                  "sentiment": {
//                    "label": "neutral",
//                    "score": 0.5
//                  }
//                }
//                """,
//                turn,
//                defaultTitles[index],
//                defaultContents[index],
//                9 + (turn % 3),
//                categories[index]
//            );
//
//            return objectMapper.readTree(defaultNewsJson);
//
//        } catch (Exception e) {
//            log.error("기본 뉴스 데이터 생성 중 오류 발생", e);
//            return null;
//        }
//    }

    /**
     * GPT API 응답을 Scenario 객체로 변환
     */
    private Scenario parseGptResponseToScenario(String gptResponse, JsonNode newsData) throws Exception {
        JsonNode scenarioJson = objectMapper.readTree(gptResponse);

        // 기본 시나리오 정보
        String title = scenarioJson.path("title").asText();
        String content = scenarioJson.path("content").asText();

        // spawn conditions 파싱
        SpawnConditionsDoc spawn = parseSpawnConditions(scenarioJson.path("conditions"));

        // choices 파싱
        Map<String, ChoiceDoc> choices = parseChoices(scenarioJson.path("choices"));

        // related article 생성 (뉴스 기반)
//        RelatedArticleDoc relatedArticle = new RelatedArticleDoc(
//            newsData.path("title").asText(),
//            newsData.path("source_url").asText()
//        );
        // 보도자료 패스
        RelatedArticleDoc relatedArticle = new RelatedArticleDoc(
                "",
                ""
        );

        // 임시 NPC (실제로는 DB에서 조회하거나 별도 로직 필요)
        Npc defaultNpc = createDefaultNpc();
        // category = newsData.path("categories").path("major_categories").get(0).path("category").asText()
        // 교체 가능
        Npc npc = npcService.getNpcByCategoryPlainJava(newsData.path("categories").path("major_categories").get(0).path("category").asText());

        log.info("here");

        return Scenario.builder()
            .title(title)
            .content(content)
            .npc(npc)
            .spawn(spawn)
            .choices(choices)
            .relatedArticle(relatedArticle)
            .build();
    }


    /**
     * spawn conditions 파싱
     */
    private SpawnConditionsDoc parseSpawnConditions(JsonNode conditionsNode) {
        List<ConditionEntryDoc> conditions = new ArrayList<>();

        if (conditionsNode.isArray()) {
            for (JsonNode conditionNode : conditionsNode) {
                String minorCategoryStr = conditionNode.path("minorCategory").asText();
                String operatorStr = conditionNode.path("operator").asText();
                double threshold = conditionNode.path("threshold").asDouble();

//                try {
//                    MinorCategory category = MinorCategory.valueOf(minorCategoryStr);
//                    ConditionOperator operator = ConditionOperator.valueOf(operatorStr);
//
//                    conditions.add(new ConditionEntryDoc(category, operator, threshold));
//                } catch (IllegalArgumentException e) {
//                    log.warn("잘못된 조건 파라미터: category={}, operator={}", minorCategoryStr, operatorStr);
//                }
            }
        }

        return new SpawnConditionsDoc(conditions);
    }

    /**
     * choices 파싱
     */
    private Map<String, ChoiceDoc> parseChoices(JsonNode choicesNode) {
        Map<String, ChoiceDoc> choices = new HashMap<>();

        if (choicesNode.isObject()) {
            choicesNode.fields().forEachRemaining(entry -> {
                String choiceKey = entry.getKey();
                JsonNode choiceValue = entry.getValue();

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

    /**
     * 개별 choice 파싱
     */
    private ChoiceDoc parseChoice(JsonNode choiceNode) {
        String code = choiceNode.path("code").asText();
        String content = choiceNode.path("content").asText();

        // effect 파싱
        EffectDoc effect = parseEffect(choiceNode.path("effect"));

        // pressRelease 파싱
        PressReleaseDoc pressRelease = parsePressRelease(choiceNode.path("pressRelease"));

        // comments 파싱
        List<String> comments = new ArrayList<>();
        JsonNode commentsNode = choiceNode.path("comments");
        if (commentsNode.isArray()) {
            commentsNode.forEach(comment -> comments.add(comment.asText()));
        }

        return new ChoiceDoc(code, content, effect, pressRelease, comments);
    }


    static final double WEIGHT_VALUE = 0.5;
    /**
     * effect 파싱
     *
     *  원래값 남겨두려다 그냥 지워버림
     */
    private EffectDoc parseEffect(JsonNode effectNode) {
        // scores 파싱
        JsonNode scoresNode = effectNode.path("scores");
        EffectScoresDoc scores = new EffectScoresDoc(
            scoresNode.path("economy").asInt(),
            scoresNode.path("defense").asInt(),
            scoresNode.path("publicSentiment").asInt(),
            scoresNode.path("environment").asInt()
        );

        // weights 파싱
//        JsonNode weightsNode = effectNode.path("weights");
        EffectWeightsDoc weights = new EffectWeightsDoc(
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

    private EffectDoc parseEffectExceptWeights(JsonNode effectNode) {
        // scores 파싱
        JsonNode scoresNode = effectNode.path("scores");
        EffectScoresDoc scores = new EffectScoresDoc(
                scoresNode.path("economy").asInt(),
                scoresNode.path("defense").asInt(),
                scoresNode.path("publicSentiment").asInt(),
                scoresNode.path("environment").asInt()
        );

        // weights 파싱
//        JsonNode weightsNode = effectNode.path("weights");
        EffectWeightsDoc weights = new EffectWeightsDoc(
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

    /**
     * pressRelease 파싱
     */
    private PressReleaseDoc parsePressRelease(JsonNode pressReleaseNode) {
        String title = pressReleaseNode.path("title").asText();
        String content = pressReleaseNode.path("content").asText();

        return new PressReleaseDoc(title, content, null); // imageUrl은 보류
    }

    /**
     * 기본 NPC 생성 (임시)
     * 실제로는 DB에서 조회하거나 별도 로직 필요
     */
    //(1,  'ECO_1', '과학자',         '["경제","과학"]'::jsonb,         NULL, now(), now()),
    private Npc createDefaultNpc() {
        return Npc.builder()
            .id(1L) // 임시 ID
            .name("과학자")
                .image(Image.builder()
                        .url("https://j13c203.p.ssafy.io/newtopia-img/public/public/2025/09/17/9ecb2ef7-b664-499e-b563-b24914d3e9de.jpg")
                        .build())
            .build();

    }

}
