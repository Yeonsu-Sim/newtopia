package io.ssafy.p.i13c203.gameserver.common.ai;


/**
 * GMS Open AI 호출 유틸리티
 */
public interface OpenAiClient {

    /**
     * Open AI 모델 api 호출
     * @param model: 대상 모델
     * @param systemPrompt: 행동 지침 프롬프트
     * @param userPrompt: 입력 데이터 프롬프트
     * @return
     */
    String chatCompletion(String model, String systemPrompt, String userPrompt);
}
