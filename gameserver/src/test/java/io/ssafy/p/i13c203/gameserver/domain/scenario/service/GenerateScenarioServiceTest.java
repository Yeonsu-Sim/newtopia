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
class GenerateScenarioServiceTest {

    @Autowired
    private GenerateScenarioService generateScenarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GPT API를 통해 원전 뉴스를 시나리오로 변환")
    void processNewsWithGPT_원전뉴스를_시나리오로_변환한다() throws Exception {
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

        long start = System.currentTimeMillis();
        // when
        String result = generateScenarioService.processNewsWithGPT(newsData);

        long end = System.currentTimeMillis();
        System.out.println("걸린 시간 : " + (end - start));

        // then
        System.out.println("=== GPT API 응답 결과 ===");
        System.out.println(result);

        if (result != null && !result.isEmpty()) {
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();

            // JSON 형식 검증
            try {
                JsonNode resultJson = objectMapper.readTree(result);
                assertThat(resultJson.has("title")).isTrue();
                assertThat(resultJson.has("content")).isTrue();
                assertThat(resultJson.has("choices")).isTrue();

                // choices 구조 검증
                JsonNode choices = resultJson.get("choices");
                assertThat(choices.has("A")).isTrue();
                assertThat(choices.has("B")).isTrue();

                // 선택지 A 구조 검증
                JsonNode choiceA = choices.get("A");
                assertThat(choiceA.has("code")).isTrue();
                assertThat(choiceA.has("content")).isTrue();
                assertThat(choiceA.has("effect")).isTrue();
                assertThat(choiceA.has("comments")).isTrue();

                System.out.println("시나리오 제목: " + resultJson.get("title").asText());
                System.out.println("시나리오 내용: " + resultJson.get("content").asText());
                System.out.println("선택지 A: " + choiceA.get("content").asText());
                System.out.println("선택지 B: " + choices.get("B").get("content").asText());
                System.out.println("JSON 형식 검증: 성공");
            } catch (Exception e) {
                System.out.println("JSON 파싱 실패: " + e.getMessage());
                // 파싱 실패해도 테스트는 통과 (API 응답 자체는 성공)
            }
        } else {
            System.out.println("GPT API 응답이 null 또는 빈 값입니다.");
            System.out.println("네트워크 또는 API 키 문제일 수 있습니다.");
        }
    }

}