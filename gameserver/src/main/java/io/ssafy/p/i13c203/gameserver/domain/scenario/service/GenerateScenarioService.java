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
import java.nio.charset.StandardCharsets;
import org.springframework.http.converter.StringHttpMessageConverter;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenerateScenarioService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // DTO 정의
    record Message(String role, String content) {}
    record ChatRequest(String model, List<Message> messages) {}

//    @Value("${app.openai.api-key}")
    private String openaiApiKey;

//    @Value("${app.openai.api-url:https://api.openai.com/v1/chat/completions}")
    private String openaiApiUrl;

    /**
     * GPT API를 통해 뉴스 데이터를 시나리오로 가공
     */
    public String processNewsWithGPT(JsonNode newsData) {
        try {
            // 메시지 구성
            List<Message> messages = createMessages(newsData);

            // model 확인
            // "gpt-4.1",  "gpt-5-mini"
            // 요청 객체 생성
            ChatRequest chatRequest = new ChatRequest("gpt-5-mini", messages);

            // JSON 문자열로 변환 (UTF-8 보장)
            String jsonBody = objectMapper.writeValueAsString(chatRequest);

            // GPT API 호출 (문자열 직접 전송)
            String apiUrl = "https://gms.ssafy.io/gmsapi/api.openai.com/v1/chat/completions";


            String response = callGptApiWithString(jsonBody, apiUrl);


            log.info("response: {}", response);
            return response;

        } catch (Exception e) {
            log.error("GPT API 호출 중 오류 발생", e);
            return null;
        }
    }

    /**
     * GPT API 호출 (문자열 직접 전송 - UTF-8 인코딩 보장)
     */
    private String callGptApiWithString(String jsonBody, String apiUrl) {
        try {
            // RestTemplate UTF-8 설정
            RestTemplate customRestTemplate = new RestTemplate();
            customRestTemplate.getMessageConverters().removeIf(c -> c instanceof StringHttpMessageConverter);
            customRestTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

            // 헤더 설정
            HttpHeaders headers = createApiHeaders();

            // 요청 바디 로깅 (디버깅용)
            log.info("=== API 요청 디버깅 (문자열 직접 전송) ===");
            log.info("URL: {}", apiUrl);
            log.info("Headers: {}", headers);
            log.info("Request Body Length: {} chars", jsonBody.length());
            log.info("Request Body: {}", jsonBody);

            // 요청 생성 (문자열로 직접 전송)
            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

            // API 호출
            ResponseEntity<String> response = customRestTemplate.postForEntity(apiUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode responseJson = objectMapper.readTree(response.getBody());

                // 토큰 사용량 로깅
                if (responseJson.has("usage")) {
                    JsonNode usage = responseJson.path("usage");
                    int promptTokens = usage.path("prompt_tokens").asInt();
                    int completionTokens = usage.path("completion_tokens").asInt();
                    int totalTokens = usage.path("total_tokens").asInt();

                    log.info("=== 토큰 사용량 ===");
                    log.info("프롬프트 토큰: {}", promptTokens);
                    log.info("응답 토큰: {}", completionTokens);
                    log.info("총 토큰: {}", totalTokens);
                }

                return responseJson.path("choices").get(0).path("message").path("content").asText();

            } else {
                log.error("GPT API 호출 실패: {}", response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            log.error("GPT API 호출 중 오류 발생", e);
            return null;
        }
    }

    /**
     * 뉴스 데이터로부터 GPT API 메시지 생성
     */
    private List<Message> createMessages(JsonNode newsData) {
        return List.of(
            new Message("developer", "당신은 정치 시뮬레이션 게임의 시나리오작가입니다. 반드시 유효한 JSON 형식으로만 응답해주세요."),
            new Message("user", String.format("""
                당신은 정치 시뮬레이션 게임의 시나리오 작가입니다.
                주어진 뉴스를 바탕으로 아래 시나리오 객체 형식에 맞게 JSON을 생성해주세요.

                【뉴스 정보】
                제목: %s
                내용: %s
                카테고리: %s
                감정: %s

                【시나리오 객체 구조】
                - title: 시나리오 제목 (20자 이내)
                - content: 상황 설명 (50자 이내, 게임다운 톤앤매너)
                - conditions: 등장 조건 (배열, 각 조건은 다음을 포함)
                  - minorCategory: 16개 중분류 중 하나
                  - operator: "LESS_THAN" | "MORE_THAN"
                  - threshold: 정수
                - choices: { "A": Choice, "B": Choice }
                  - code: "A" 또는 "B"
                  - content: 선택지 설명
                  - effect:
                    - scores: { economy, defense, environment, publicSentiment } (정수 -100~100)
                    - weights: { 16개 중분류 가중치, 실수 0.0~1.0 }
                  - pressRelease: { title, content } (기사 느낌)
                  - comments: 국민 반응 문자열 배열

                【요구사항】
                1. 반드시 유효한 JSON만 응답할 것
                2. scores와 weights는 각각 의미 있는 수치를 설정할 것
                3. comments는 최소 2개만 , 실제 시민 반응처럼 작성할 것
                4. pressRelease.content는 임시로 비워둘 수 있음
                5. 16개 중분류: macroeconomy, fiscalPolicy, financialMarkets, industryBusiness, militarySecurity, alliances, cyberSpace, publicSafety, publicOpinion, socialIssues, protestsStrikes, healthWelfare, climateChangeEnergy, pollutionDisaster, biodiversity, resourceManagement
                """,
                newsData.path("title").asText(),
                newsData.path("content").asText(),
                newsData.path("categories").path("major_categories").get(0).path("category").asText(),
                newsData.path("sentiment").path("label").asText()
            ))
        );
    }

    /**
     * GPT API 호출용 헤더 생성
     */
    private HttpHeaders createApiHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        headers.setBearerAuth("S13P22C203-45155ad1-760a-4e91-bebf-8a7db540fc92");
        headers.set("User-Agent", "Gameserver/1.0");
        headers.set("Accept", "application/json");
        return headers;
    }

}