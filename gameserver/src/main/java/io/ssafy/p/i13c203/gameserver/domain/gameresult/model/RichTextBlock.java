package io.ssafy.p.i13c203.gameserver.domain.gameresult.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RichTextBlock(
        BlockType type,
        String title,
        String richText
) implements SummaryBlock {
    public static RichTextBlock of(String title, String richText) {
        return new RichTextBlock(BlockType.RICH_TEXT, title, richText);
    }
}
