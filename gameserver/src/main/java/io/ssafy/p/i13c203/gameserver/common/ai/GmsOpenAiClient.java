package io.ssafy.p.i13c203.gameserver.common.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ssafy.p.i13c203.gameserver.common.util.Jsons;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GmsOpenAiClient implements OpenAiClient {

    private final WebClient webClient = WebClient.builder().build();
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
        try {
            return chatCompletionAsync(model, systemPrompt, userPrompt).block();
        } catch (Exception e) {
            log.error("GPT API 호출 중 오류 발생", e);
            return null;
        }
    }

    @Override
    public Mono<String> chatCompletionAsync(String model, String systemPrompt, String userPrompt) {
        String url = baseUrl + chatCompletionsPath;

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user",   "content", userPrompt)
                )
        );

        String reqJson = Jsons.toJson(requestBody);

        // ==== 요청 로그 ====
        log.info("[GMS] POST {}", url);
        log.info("[GMS] req.length={} body={}", reqJson.length(), truncate(reqJson, logPreviewLen));

        return webClient.post()
                .uri(url)
                .headers(h -> {
                    h.setContentType(MediaType.APPLICATION_JSON);
                    if (!isBlank(bearerToken)) h.setBearerAuth(bearerToken);
                    if (!isBlank(apiKey))      h.set("X-API-KEY", apiKey);
                    h.set("User-Agent", "Gameserver/1.0");
                    h.set("Accept", "application/json");
                })
                .bodyValue(reqJson)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), resp ->
                        Mono.error(new RuntimeException("GPT API 호출 실패: " + resp.statusCode()))
                )
                .bodyToMono(String.class)
                .map(responseBody -> {
                    try {
                        JsonNode responseJson = objectMapper.readTree(responseBody);

                        // 토큰 사용량 로깅
                        if (responseJson.has("usage")) {
                            JsonNode usage = responseJson.path("usage");
                            log.info("=== 토큰 사용량 ===");
                            log.info("프롬프트 토큰: {}", usage.path("prompt_tokens").asInt());
                            log.info("응답 토큰: {}", usage.path("completion_tokens").asInt());
                            log.info("총 토큰: {}", usage.path("total_tokens").asInt());
                        }

                        String content = responseJson.path("choices").get(0).path("message").path("content").asText();
                        log.info("Message Content: {}", content);
                        return content;

                    } catch (Exception e) {
                        throw new RuntimeException("GPT 응답 파싱 실패", e);
                    }
                });
    }


    private static boolean isBlank(String s) { return s == null || s.isBlank(); }

    private static String truncate(String s, int n) {
        if (s == null) return null;
        return s.length() <= n ? s : s.substring(0, n) + "...(truncated)";
    }
}
