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
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
import io.ssafy.p.i13c203.gameserver.domain.scenario.repository.ScenarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioServiceV2 implements ScenarioService{

    private final SimpleNewsService simpleNewsService;
    private final GenerateScenarioService generateScenarioService;


    @Override
    public Scenario firstScenario() {
        return null;
    }




    // 1. 게임 상태를 분석하여 적절한 뉴스 데이터 조회 및 시나리오 생성
    @Override
    public Scenario nextScenario(Game game, int nextTurn) {
        try {
            // 게임 상태에 따라 적절한 뉴스 조회
            JsonNode newsData = simpleNewsService.getNewsForGameState(game);
            
            if (newsData != null) {
                log.info("뉴스 조회 성공: {}", newsData.get("title").asText());
                
                // TODO: JsonNode(뉴스 데이터)를 기반으로 Scenario 객체 생성
                // 1. 뉴스 제목, 내용을 시나리오 카드로 변환
                // 2. 선택지 생성 (뉴스 카테고리에 맞는 정책 선택지)
                // 3. NPC 설정
                // 4. 효과 설정 (countryStats, choiceWeights 변화)
                
                return createScenarioFromNews(newsData);
            } else {
                log.warn("적절한 뉴스를 찾지 못했습니다. 기본 시나리오를 반환합니다.");
                return null;
            }
            
        } catch (Exception e) {
            log.error("시나리오 생성 중 오류 발생", e);
            return null;
        }
    }

    /**
     * 뉴스 데이터를 기반으로 시나리오 생성
     */
    private Scenario createScenarioFromNews(JsonNode newsData) {
        log.info("뉴스 기반 시나리오 생성: {}", newsData.get("title").asText());

        String s = generateScenarioService.processNewsWithGPT(newsData);

        return null;
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

}
