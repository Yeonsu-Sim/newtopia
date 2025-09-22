package io.ssafy.p.i13c203.gameserver.domain.gameresult.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BulletsBlock(
        BlockType type,
        String title,
        List<String> bullets
) implements SummaryBlock {
    public static BulletsBlock of(String title, List<String> bullets) {
        return new BulletsBlock(BlockType.BULLETS, title, bullets);
    }
}

