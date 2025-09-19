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
            ChatRequest chatRequest = new ChatRequest("gpt-4.1", messages);

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
     *  1.  중분류 카테고리 제거
     *          0.5로 하드코딩
     *  2.  보도 자료 제거
     *
     */
    private List<Message> createMessages(JsonNode newsData) {

//        return createMessagesRemoveMinorCategory(newsData);
        return createMessagesRemoveMinorCategoryAndPressRelease(newsData);
//        return List.of(

//            new Message("developer", "당신은 정치 시뮬레이션 게임의 시나리오작가입니다. 반드시 유효한 JSON 형식으로만 응답해주세요."),
//            new Message("user", String.format("""
//                당신은 정치 시뮬레이션 게임의 시나리오 작가입니다.
//                주어진 뉴스를 바탕으로 아래 시나리오 객체 형식에 맞게 JSON을 생성해주세요.
//
//                【뉴스 정보】
//                제목: %s
//                내용: %s
//                카테고리: %s
//                감정: %s
//
//                【시나리오 객체 구조】
//                - title: 시나리오 제목 (20자 이내)
//                - content: 상황 설명 (50자 이내, 게임다운 톤앤매너)
//                - conditions: 등장 조건 (배열, 각 조건은 다음을 포함)
//                  - minorCategory: 16개 중분류 중 하나
//                  - operator: "LESS_THAN" | "MORE_THAN"
//                  - threshold: 정수
//                - choices: { "A": Choice, "B": Choice }
//                  - code: "A" 또는 "B"
//                  - content: 선택지 설명
//                  - effect:
//                    - scores: { economy, defense, environment, publicSentiment } (정수 -100~100)
//                  - pressRelease: { title, content } (기사 느낌)
//                  - comments: 국민 반응 문자열 배열
//
//                【요구사항】
//                1. 반드시 유효한 JSON만 응답할 것
//                2. scores와 weights는 각각 의미 있는 수치를 설정할 것
//                3. comments는 최소 2개만 , 실제 시민 반응처럼 작성할 것
//                4. pressRelease.content는 임시로 비워둘 수 있음
//                5. 16개 중분류: macroeconomy, fiscalPolicy, financialMarkets, industryBusiness, militarySecurity, alliances, cyberSpace, publicSafety, publicOpinion, socialIssues, protestsStrikes, healthWelfare, climateChangeEnergy, pollutionDisaster, biodiversity, resourceManagement
//                """,
//                newsData.path("title").asText(),
//                newsData.path("content").asText(),
//                newsData.path("categories").path("major_categories").get(0).path("category").asText(),
//                newsData.path("sentiment").path("label").asText()
//            ))
//        );
    }

    // 중분류 카테고리 제거
    private List<Message> createMessagesRemoveMinorCategory(JsonNode newsData) {
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
                  - operator: "LESS_THAN" | "MORE_THAN"
                  - threshold: 정수
                - choices: { "A": Choice, "B": Choice }
                  - code: "A" 또는 "B"
                  - content: 선택지 설명
                  - effect:
                    - scores: { economy, defense, environment, publicSentiment } (정수 -100~100)
                  - pressRelease: { title, content } (기사 느낌)
                  - comments: 국민 반응 문자열 배열

                【요구사항】
                1. 반드시 유효한 JSON만 응답할 것
                2. scores와 weights는 각각 의미 있는 수치를 설정할 것
                3. comments는 최소 2개만 , 실제 시민 반응처럼 작성할 것
                4. pressRelease.content는 임시로 비워둘 수 있음
                """,
                        newsData.path("title").asText(),
                        newsData.path("content").asText(),
                        newsData.path("categories").path("major_categories").get(0).path("category").asText(),
                        newsData.path("sentiment").path("label").asText()
                ))
        );
    }

    //  카테고리 + 보도 자료 제거
    private List<Message> createMessagesRemoveMinorCategoryAndPressRelease(JsonNode newsData) {
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
                                  - operator: "LESS_THAN" | "MORE_THAN"
                                  - threshold: 정수
                                - choices: { "A": Choice, "B": Choice }
                                  - code: "A" 또는 "B"
                                  - content: 선택지 설명
                                  - effect:
                                    - scores: { economy, defense, environment, publicSentiment } (정수)
                                  - comments: 국민 반응 문자열 배열
                                                
                                【점수(밸런스) 규칙】
                                1) 각 score는 -20 이상 20 이하의 정수여야 한다.
                                2) “선택지 내용 ↔ scores”가 논리적으로 일치해야 한다.
                                   - 예: 환경 보호 정책 → environment는 +(소폭~중간폭), 규제 강화로 경제 부담 → economy는 -(소폭)
                                3) 과도한 급등락 방지(지표는 0~100에서 끝남):
                                   - 일반 뉴스(보통 강도): 주효과 ±8~±18, 부수 효과 ±3~±10, 상쇄 효과(트레이드오프) ±2~±8
                                   - 큰 이슈(강한 강도): 주효과 최대 ±25까지 허용하되, 반드시 1개 이상 상쇄 효과 포함
                                   - 아주 경미한 이슈(약한 강도): 모든 효과 절대값을 3~10 사이로 제한
                                   (강도는 기사 내용/감정에서 추론하되, 과도한 값 사용 금지)
                                4) 네 지표가 모두 같은 방향(전부 + 또는 전부 -)이 되지 않도록 최소 1개 이상의 트레이드오프를 포함한다.
                                5) 의미 없는 0 남발 금지: 최소 2개 이상의 지표에 |score| ≥ 3을 부여한다.
                                6) A/B는 서로 다른 전략과 결과를 보여야 하며, 동일/유사한 분포를 피한다.
                                                
                                【comments 규칙】
                                - 최소 2개 이상, 실제 시민 반응처럼 구체적으로 작성(이해관계·우려·지지 등 혼합 가능)
                                - 과도한 비속어 금지, 짧고 명확하게
                                                
                                【출력 형식 요구사항】
                                - 반드시 유효한 JSON만 출력
                                - 설명 문구나 여는/닫는 텍스트 없이 JSON만 반환
                                                
                
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