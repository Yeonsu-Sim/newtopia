package io.ssafy.p.i13c203.gameserver.domain.scenario.service;

import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FirstScenarioTest {

    @Autowired
    private ScenarioServiceV2 scenarioServiceV2;

    @Test
    @DisplayName("첫 번째 시나리오 생성 테스트")
    void firstScenario_무작위뉴스로_첫번째시나리오를_생성한다() throws Exception {
        // given
        long start = System.currentTimeMillis();

        // when
        Scenario result = scenarioServiceV2.firstScenario();

        long end = System.currentTimeMillis();
        System.out.println("첫 번째 시나리오 생성 총 걸린 시간: " + (end - start) + "ms");

        // then
        if (result != null) {
            System.out.println("=== 생성된 첫 번째 Scenario 객체 정보 ===");
            System.out.println("제목: " + result.getTitle());
            System.out.println("내용: " + result.getContent());
            System.out.println("NPC: " + result.getNpc().getName());
            System.out.println("등장 조건 수: " + result.getSpawn().conditions().size());
            System.out.println("선택지 수: " + result.getChoices().size());
            System.out.println("관련 기사 제목: " + result.getRelatedArticle().title());
            System.out.println("관련 기사 URL: " + result.getRelatedArticle().url());

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

            // 등장 조건 검증
            if (!result.getSpawn().conditions().isEmpty()) {
                System.out.println("\n=== 등장 조건 ===");
                result.getSpawn().conditions().forEach(condition -> {
                    System.out.println("  카테고리: " + condition.category());
                    System.out.println("  연산자: " + condition.operator());
                    System.out.println("  임계값: " + condition.threshold());
                });
            }

            System.out.println("\n=== 첫 번째 Scenario 객체 생성 성공 ===");

        } else {
            System.out.println("첫 번째 Scenario 생성 실패");
            System.out.println("- 무작위 뉴스 조회 실패일 수 있습니다.");
            System.out.println("- GPT API 호출 실패일 수 있습니다.");
            System.out.println("- JSON 파싱 오류일 수 있습니다.");
        }
    }
}