package io.ssafy.p.i13c203.gameserver.domain.gameresult.model;

import com.fasterxml.jackson.annotation.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BulletsBlock.class, name = "BULLETS"),
        @JsonSubTypes.Type(value = TextBlock.class, name = "TEXT"),
        @JsonSubTypes.Type(value = RichTextBlock.class, name = "RICH_TEXT")
        // 새 타입 필요 시 여기만 1줄 추가
})
public interface SummaryBlock {
    BlockType type();      // "BULLETS" | "RICH_TEXT" | "TEXT" ...
    String title();     // UI 헤더(예: "하이라이트 턴", "결말", "한줄평")
}
