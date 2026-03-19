package io.ssafy.p.i13c203.gameserver.common.ai;

import reactor.core.publisher.Mono;

/**
 * GMS Open AI 호출 유틸리티
 */
public interface OpenAiClient {

    /**
     * Open AI 모델 api 호출 (블로킹)
     */
    String chatCompletion(String model, String systemPrompt, String userPrompt);

    /**
     * Open AI 모델 api 호출 (논블로킹, Mono 반환)
     * 예외는 Mono.error()로 전파됨
     */
    Mono<String> chatCompletionAsync(String model, String systemPrompt, String userPrompt);
}
