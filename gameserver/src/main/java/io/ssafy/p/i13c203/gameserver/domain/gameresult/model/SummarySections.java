package io.ssafy.p.i13c203.gameserver.domain.gameresult.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

/** 섹션 키(예: "highlights", "ending", ...) -> 섹션 블록 */
@ToString
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)                  // ← 알 수 없는 필드는 역직렬화 시 무시
public class SummarySections {
    private Map<String, SummaryBlock> blocks = new LinkedHashMap<>();

    public static SummarySections empty() {
        return new SummarySections();
    }

    public SummarySections put(String key, SummaryBlock block) {
        this.blocks.put(key, block);
        return this;
    }

    public SummaryBlock get(String key) { return this.blocks.get(key); }
}
