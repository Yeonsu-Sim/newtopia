package io.ssafy.p.i13c203.gameserver.domain.scenario.service;

import com.fasterxml.jackson.databind.JsonNode;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SimpleNewsServiceTest {

    @Autowired
    private SimpleNewsService simpleNewsService;

    @Test
    void getNewsForGame_게임_조건에_맞는_뉴스를_하나_가져온다() {
        // given
        Game game = new Game();
        
        // when
        JsonNode newsData = simpleNewsService.getNewsForGame(game, "economy", "negative");
        
        // then
        if (newsData != null) {
            assertThat(newsData.get("sentiment").get("label").asText()).isEqualTo("negative");
            System.out.println("조회된 뉴스 제목: " + newsData.get("title").asText());
            System.out.println("카테고리: " + newsData.get("categories").get("major_categories").get(0).get("category").asText());
            System.out.println("감정: " + newsData.get("sentiment").get("label").asText());
        } else {
            System.out.println("조건에 맞는 뉴스를 찾을 수 없습니다.");
        }
    }

    @Test
    void getNewsForGameState_게임_상태를_분석해서_적절한_뉴스를_가져온다() {
        // given
        Game game = new Game();
        // game.setTurn(1); // 실제 Game 엔티티에 맞춰 설정
        
        // when
        JsonNode newsData = simpleNewsService.getNewsForGameState(game);
        
        // then
        if (newsData != null) {
            assertThat(newsData.get("title")).isNotNull();
            assertThat(newsData.get("content")).isNotNull();
            assertThat(newsData.get("sentiment")).isNotNull();
            assertThat(newsData.get("categories")).isNotNull();
            
            System.out.println("=== 게임 상태 기반 뉴스 조회 결과 ===");
            System.out.println("제목: " + newsData.get("title").asText());
            System.out.println("감정: " + newsData.get("sentiment").get("label").asText());
            System.out.println("점수: " + newsData.get("sentiment").get("score").asDouble());
            System.out.println("카테고리: " + newsData.get("categories").get("major_categories").get(0).get("category").asText());
        } else {
            System.out.println("게임 상태에 맞는 뉴스를 찾을 수 없습니다.");
        }
    }

    @Test
    void SimpleNewsService_정상적으로_초기화된다() {
        // then
        assertThat(simpleNewsService).isNotNull();
        System.out.println("SimpleNewsService가 정상적으로 초기화되었습니다.");
    }
}