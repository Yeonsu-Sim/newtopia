package io.ssafy.p.i13c203.gameserver.domain.scenario.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.domain.game.model.CountryStats;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.EffectDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.EffectScoresDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.ChoiceDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.RelatedArticleDoc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenerateScenarioService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

//    @Value("${app.openai.api-key}")
    private String openaiApiKey;

//    @Value("${app.openai.api-url:https://api.openai.com/v1/chat/completions}")
    private String openaiApiUrl;

    /**
     * GPT API를 통해 뉴스 데이터를 시나리오로 가공
     */
    public String processNewsWithGPT(JsonNode newsData) {

            // GPT API 메시지 구성
            List<Map<String, String>> messages = new ArrayList<>();
            
            // Developer 메시지 (한국어 응답 지시)
            Map<String, String> developerMessage = new HashMap<>();
            developerMessage.put("role", "developer");

            // User 메시지 (실제 프롬프트)
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", String.format("""
                당신은 정치 시뮬레이션 게임의 시나리오 작가입니다. 주어진 뉴스를 바탕으로 대통령 게임 시나리오를 만들어주세요.

                【뉴스 정보】
                제목: %s
                내용: %s
                카테고리: %s
                감정: %s

                【요구사항】
                1. 뉴스를 게임 상황으로 각색하여 대통령이 결정해야 할 상황 만들기
                2. A, B 두 개의 대조적인 반응/대응 선택지 제공
                3. 각 선택지의 효과: scores(대분류 점수), weights(중분류 가중치), comments(국민 댓글) 포함
                4. 16개 중분류 카테고리의 가중치를 0.0~1.0 범위로 설정

                반드시 다음 JSON 형식으로만 응답해주세요:
                {
                  "title": "시나리오 제목 (50자 이내)",
                  "content": "상황 설명 (300자 이내, 게임다운 톤앤매너)",
                  "choices": {
                    "A": {
                      "code": "A",
                      "content": "선택지 A 내용",
                      "effect": {
                        "scores": {
                          "economy": 10,
                          "defense": -5,
                          "environment": 0,
                          "publicSentiment": 8
                        },
                        "weights": {
                          "macroeconomy": 0.8, "fiscalPolicy": 0.6, "financialMarkets": 0.7, "industryBusiness": 0.9,
                          "militarySecurity": 0.3, "alliances": 0.4, "cyberSpace": 0.2, "publicSafety": 0.1,
                          "publicOpinion": 0.7, "socialIssues": 0.5, "protestsStrikes": 0.6, "healthWelfare": 0.8,
                          "climateChangeEnergy": 0.1, "pollutionDisaster": 0.2, "biodiversity": 0.3, "resourceManagement": 0.4
                        }
                      },
                      "comments": [
                        "선택지 A에 대한 국민 댓글 1",
                        "선택지 A에 대한 국민 댓글 2",
                        "선택지 A에 대한 국민 댓글 3"
                      ]
                    },
                    "B": {
                      "code": "B",
                      "content": "선택지 B 내용",
                      "effect": {
                        "scores": {
                          "economy": -8,
                          "defense": 12,
                          "environment": 15,
                          "publicSentiment": -3
                        },
                        "weights": {
                          "macroeconomy": 0.2, "fiscalPolicy": 0.4, "financialMarkets": 0.3, "industryBusiness": 0.1,
                          "militarySecurity": 0.9, "alliances": 0.8, "cyberSpace": 0.7, "publicSafety": 0.6,
                          "publicOpinion": 0.3, "socialIssues": 0.4, "protestsStrikes": 0.2, "healthWelfare": 0.5,
                          "climateChangeEnergy": 0.9, "pollutionDisaster": 0.8, "biodiversity": 0.7, "resourceManagement": 0.6
                        }
                      },
                      "comments": [
                        "선택지 B에 대한 국민 댓글 1",
                        "선택지 B에 대한 국민 댓글 2",
                        "선택지 B에 대한 국민 댓글 3"
                      ]
                    }
                  }
                }
                """,
                newsData.path("title").asText(),
                newsData.path("content").asText(), 
                newsData.path("categories").path("major_categories").get(0).path("category").asText(),
                newsData.path("sentiment").path("label").asText()
            ));
            
            messages.add(developerMessage);
            messages.add(userMessage);
            
            // API 요청 body 구성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-5");
            requestBody.put("messages", messages);



        
        return null;
    }

}