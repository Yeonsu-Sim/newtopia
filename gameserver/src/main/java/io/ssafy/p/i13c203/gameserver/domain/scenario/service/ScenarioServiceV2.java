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
                return createDefaultScenario(game, nextTurn);
            }
            
        } catch (Exception e) {
            log.error("시나리오 생성 중 오류 발생", e);
            return createDefaultScenario(game, nextTurn);
        }
    }

    /**
     * 뉴스 데이터를 기반으로 시나리오 생성
     */
    private Scenario createScenarioFromNews(JsonNode newsData) {
        log.info("뉴스 기반 시나리오 생성: {}", newsData.get("title").asText());
        
        return generateScenarioService.createScenarioFromNews(newsData, null);
    }

    /**
     * 기본 시나리오 생성 (뉴스를 찾지 못한 경우)
     */
    private Scenario createDefaultScenario(Game game, int turn) {
        log.info("기본 시나리오 생성 - 턴: {}", turn);
        
        // 기본 뉴스 데이터 구조 생성
        JsonNode defaultNewsData = createDefaultNewsData(turn);
        
        return generateScenarioService.createScenarioFromNews(defaultNewsData, game);
    }
    
    /**
     * 기본 뉴스 데이터 생성
     */
    private JsonNode createDefaultNewsData(int turn) {
        try {
            String defaultJson = String.format("""
                {
                  "title": "제%d턴 정기 국정 현안",
                  "content": "국정 운영 과정에서 중요한 결정이 필요한 시점입니다. 현재 상황을 종합적으로 검토하여 적절한 정책 방향을 결정해야 합니다.",
                  "categories": {
                    "major_categories": [
                      {
                        "category": "publicSentiment",
                        "confidence": 0.8
                      }
                    ]
                  },
                  "sentiment": {
                    "label": "neutral",
                    "score": 0.5
                  },
                  "source_url": "internal://default-scenario"
                }
                """, turn);
            
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(defaultJson);
            
        } catch (Exception e) {
            log.error("기본 뉴스 데이터 생성 실패", e);
            return null;
        }
    }
}
