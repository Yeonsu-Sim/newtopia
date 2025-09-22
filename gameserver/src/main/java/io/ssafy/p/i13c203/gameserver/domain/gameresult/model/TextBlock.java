package io.ssafy.p.i13c203.gameserver.domain.gameresult.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TextBlock(
        BlockType type,
        String title,
        String text
) implements SummaryBlock {
    public static TextBlock of(String title, String text) {
        return new TextBlock(BlockType.TEXT, title, text);
    }
}
