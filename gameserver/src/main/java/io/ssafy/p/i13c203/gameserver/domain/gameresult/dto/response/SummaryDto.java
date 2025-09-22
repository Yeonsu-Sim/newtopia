package io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;
import java.util.Map;

public record SummaryDto(
        String status,          // "PENDING" | "READY" | "ERROR"  (문자열 유지: 백엔드 Enum 매핑은 서비스에서)
        String promptHash,      // 동기 엔드포인트에서는 null 가능
        Sections sections,      // READY일 때만 채움
        String subscribeUrl     // 비동기일 때만 채움
) {
    /**
     * sections.blocks: "highlights", "ending" 등 자유로운 키에 다양한 블록 타입을 담을 수 있음
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Sections(
            Map<String, Block> blocks
    ) {}

    // ========= 섹션 블록: type 기반 폴리모픽 =========
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = BulletsBlock.class, name = "BULLETS"),
            @JsonSubTypes.Type(value = RichTextBlock.class, name = "TEXT"),
            @JsonSubTypes.Type(value = RichTextBlock.class, name = "RICH_TEXT")
            // 새 블록 타입 추가 시 여기에 1줄만 추가
    })
    public interface Block {
        String type();   // "BULLETS" | "TEXT" | "RICH_TEXT" ...
        String title();  // UI에 표시할 블록 제목 (예: "하이라이트 턴", "결말". "한줄평")
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BulletsBlock(
            String type,            // 항상 "BULLETS"
            String title,
            List<String> bullets
    ) implements Block {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RichTextBlock(
            String type,            // 항상 "RICH_TEXT"
            String title,
            String text
    ) implements Block {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TextBlock(
            String type,            // 항상 "TEXT"
            String title,
            String text
    ) implements Block {}
}