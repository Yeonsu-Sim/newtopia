package io.ssafy.p.i13c203.gameserver.domain.scenario.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ScenarioServiceV2Test {

    @Autowired
    private ScenarioServiceV3 scenarioServiceV2;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("뉴스 데이터를 기반으로 완전한 Scenario 객체 생성")
    void nextScenario_뉴스데이터를_기반으로_시나리오객체를_생성한다() throws Exception {
        // given - 실제 뉴스 데이터
        String newsJson = """
            {
              "source_url": "https://n.news.naver.com/mnews/article/366/0001102909",
              "title": "대형 원전 4기에 SMR 추가… 美와 협력 시작됐다",
              "content": "한국수력원자력(한수원) 등 팀코리아(Team Korea)와 미국 웨스팅하우스와의 원전 협력이 가동됐다. 이재명 대통령이 지난 25일(현지 시각) 도널드 트럼프 미국 대통령과 정상회담을 가진 가운데, 한국은 대형 원전 4기와 소형모듈원전(SMR·Small Modular Reactor) 등이 투입되는 11GW(기가와트) 규모의 'AI 캠퍼스' 건설에 참여하기로 했다.",
              "published_at": "2025.08.26. 오전 10:55",
              "categories": {
                "major_categories": [
                  {
                    "category": "defense",
                    "confidence": 0.35492873191833496
                  }
                ],
                "sub_categories": {
                  "defense": [
                    {
                      "category": "allianceSecurity",
                      "confidence": 0.40251049399375916
                    }
                  ]
                }
              },
              "sentiment": {
                "label": "positive",
                "score": 0.75
              }
            }
            """;

        JsonNode newsData = objectMapper.readTree(newsJson);
        Game game = new Game();

        long start = System.currentTimeMillis();

        // when
        Scenario result = scenarioServiceV2.nextScenario(game);

        long end = System.currentTimeMillis();
        System.out.println("시나리오 생성 총 걸린 시간: " + (end - start) + "ms");

        // then
        if (result != null) {
            System.out.println("=== 생성된 Scenario 객체 정보 ===");
            System.out.println("제목: " + result.getTitle());
            System.out.println("내용: " + result.getContent());
            System.out.println("NPC: " + result.getNpc().getName());
            System.out.println("등장 조건 수: " + result.getSpawn().conditions().size());
            System.out.println("선택지 수: " + result.getChoices().size());
            System.out.println("관련 기사 제목: " + result.getRelatedArticle().title());

            // 기본 검증
            assertThat(result.getTitle()).isNotNull().isNotEmpty();
            assertThat(result.getContent()).isNotNull().isNotEmpty();
            assertThat(result.getNpc()).isNotNull();
            assertThat(result.getSpawn()).isNotNull();
            assertThat(result.getChoices()).isNotNull().hasSize(2);
            assertThat(result.getRelatedArticle()).isNotNull();

            // 선택지 상세 검증
            assertThat(result.getChoices()).containsKeys("A", "B");

            result.getChoices().forEach((key, choice) -> {
                System.out.println("\n선택지 " + key + ":");
                System.out.println("  코드: " + choice.code());
                System.out.println("  내용: " + choice.label());
                System.out.println("  경제 점수: " + choice.effect().scores().economy());
                System.out.println("  국방 점수: " + choice.effect().scores().defense());
                System.out.println("  환경 점수: " + choice.effect().scores().environment());
                System.out.println("  여론 점수: " + choice.effect().scores().publicSentiment());
                System.out.println("  댓글 수: " + choice.comments().size());
                System.out.println("  보도자료 제목: " + choice.pressRelease().title());

                // 선택지별 검증
                assertThat(choice.code()).isEqualTo(key);
                assertThat(choice.label()).isNotNull().isNotEmpty();
                assertThat(choice.effect()).isNotNull();
                assertThat(choice.effect().scores()).isNotNull();
                assertThat(choice.effect().weights()).isNotNull();
                assertThat(choice.comments()).isNotNull().isNotEmpty();
                assertThat(choice.pressRelease()).isNotNull();
            });

            System.out.println("\n=== Scenario 객체 생성 성공 ===");

        } else {
            System.out.println("Scenario 생성 실패 - API 문제이거나 파싱 오류일 수 있습니다.");
        }
    }
}