package io.ssafy.p.i13c203.gameserver.common.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ssafy.p.i13c203.gameserver.common.util.Jsons;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GmsOpenAiClient implements OpenAiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    @Value("${ssafy.gms.base-url}")
    private String baseUrl;

    @Value("${ssafy.gms.paths.chat-completions}")
    private String chatCompletionsPath;

    @Value("${ssafy.gms.bearer-token:}")
    private String bearerToken;

    @Value("${ssafy.gms.api-key:}")
    private String apiKey;

    @Value("${ssafy.gms.log-body-preview:500}")
    private int logPreviewLen;

    @Override
    public String chatCompletion(String model, String systemPrompt, String userPrompt) {

        String url = baseUrl + chatCompletionsPath; // ** 중복 붙지 않게 엄격히 관리 **

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (!isBlank(bearerToken)) headers.setBearerAuth(bearerToken);
        if (!isBlank(apiKey)) headers.set("X-API-KEY", apiKey);
        headers.set("User-Agent", "Gameserver/1.0");
        headers.set("Accept", "application/json");

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user",   "content", userPrompt)
                                   )
                                                );

        // ==== 요청 로그 (마스킹) ====
        log.info("[GMS] POST {}", url);
        log.info("[GMS] headers: {}", maskedHeaders(headers));
        String reqJson = Jsons.toJson(requestBody);
        log.info("[GMS] req.length={} body={}", reqJson.length(), truncate(reqJson, logPreviewLen));

        HttpEntity<String> request = new HttpEntity<>(reqJson, headers);

        try {

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

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

//                log.info("Response: {}", response.getBody());

                String content = responseJson.path("choices").get(0).path("message").path("content").asText();
                log.info("Message Content: {}", content);

                return content;

            } else {
                log.error("GPT API 호출 실패: {}", response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            log.error("GPT API 호출 중 오류 발생", e);
            return null;
        }
    }


    private static boolean isBlank(String s) { return s == null || s.isBlank(); }

    private static String truncate(String s, int n) {
        if (s == null) return null;
        return s.length() <= n ? s : s.substring(0, n) + "...(truncated)";
    }

    private static String maskedHeaders(HttpHeaders h) {
        HttpHeaders c = new HttpHeaders();
        c.putAll(h);
        if (c.containsKey(HttpHeaders.AUTHORIZATION)) c.set(HttpHeaders.AUTHORIZATION, "Bearer ****");
        if (c.containsKey("X-API-KEY")) c.set("X-API-KEY", "****");
        return c.toString();
    }
}
