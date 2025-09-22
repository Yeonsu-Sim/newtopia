package io.ssafy.p.i13c203.gameserver.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * 간단한 JSON 직렬화/역직렬화 유틸
 */
@Slf4j
public final class Jsons {

    private static final ObjectMapper mapper = new ObjectMapper();

    private Jsons() {
        // util class, no instance
    }

    /** 객체 → JSON 문자열 (실패 시 에러 메시지 리턴) */
    public static String toJson(Object value) {
        if (value == null) return "null";
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("[Jsons] 직렬화 실패: {}", e.getMessage(), e);
            return "{\"error\":\"json-serialization-failed\"}";
        }
    }

    /** JSON 문자열 → 객체 */
    public static <T> T fromJson(String json, Class<T> type) {
        if (json == null) return null;
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            log.error("[Jsons] 역직렬화 실패: {}", e.getMessage(), e);
            return null;
        }
    }
}
