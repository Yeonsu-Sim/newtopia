package io.ssafy.p.i13c203.gameserver.domain.scenario.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import io.ssafy.p.i13c203.gameserver.domain.game.model.CountryStats;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
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

    @Value("${app.openai.api-key}")
    private String openaiApiKey;

    @Value("${app.openai.api-url:https://api.openai.com/v1/chat/completions}")
    private String openaiApiUrl;

    /**
     * GPT API를 통해 뉴스 데이터를 시나리오로 가공
     */
    public String processNewsWithGPT(JsonNode newsData, Game game) {
        try {
            CountryStats stats = game.getCountryStats();
            String countryName = game.getCountryName();
            int currentTurn = game.getTurn();

            // GPT 프롬프트 생성
            String prompt = String.format("""
                다음 뉴스 데이터를 바탕으로 %s 국가의 대통령이 되어 게임을 진행하는 시나리오를 만들어주세요.

                현재 게임 상황:
                - 국가: %s
                - 턴: %d
                - 경제: %d/100
                - 국방: %d/100
                - 환경: %d/100
                - 민심: %d/100

                뉴스 데이터:
                제목: %s
                내용: %s
                카테고리: %s
                감정: %s

                위 뉴스를 바탕으로 대통령이 직면할 수 있는 상황과 선택지를 포함한 시나리오를 JSON 형태로 작성해주세요.
                선택지는 3개를 제공하고, 각각이 국가 지표에 미치는 영향도 포함해주세요.
                """,
                countryName, countryName, currentTurn,
                stats.getEconomy(), stats.getDefense(), stats.getEnvironment(), stats.getPublicSentiment(),
                newsData.path("title").asText(),
                newsData.path("content").asText(),
                newsData.path("categories").toString(),
                newsData.path("sentiment").toString()
            );

            // GPT API 요청 구성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);

            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 1000);
            requestBody.put("temperature", 0.7);

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openaiApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // GPT API 호출
            ResponseEntity<String> response = restTemplate.postForEntity(openaiApiUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode responseJson = objectMapper.readTree(response.getBody());
                String generatedScenario = responseJson.path("choices").get(0).path("message").path("content").asText();

                log.info("Successfully generated scenario with GPT for game: {}", game.getGameId());
                return generatedScenario;
            }

        } catch (Exception e) {
            log.error("Failed to process news with GPT", e);
        }

        return null;
    }

    /**
     * GPT 결과와 뉴스 데이터로 Scenario 객체 생성
     */
    public Scenario createScenarioFromGPTResult(String gptResult, JsonNode newsData) {
        try {
            // GPT 결과를 파싱하여 Scenario 객체 생성
            // 실제 구현에서는 GPT 응답의 JSON을 파싱하여 Scenario 필드에 매핑

            Scenario scenario = new Scenario();

            // 여기서는 예시로 기본값 설정
            // 실제로는 GPT 응답을 파싱하여 시나리오 내용, 선택지 등을 설정해야 함

            log.debug("Created scenario from GPT result with news source: {}",
                     newsData.path("source_url").asText());

            return scenario;

        } catch (Exception e) {
            log.error("Failed to create scenario from GPT result", e);
            return null;
        }
    }
}