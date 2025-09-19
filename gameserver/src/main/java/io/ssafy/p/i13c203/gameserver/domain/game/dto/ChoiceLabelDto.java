package io.ssafy.p.i13c203.gameserver.domain.game.dto;

import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.PressReleaseDoc;
import java.util.List;

public record ChoiceLabelDto(
        String code,
        String label,
        PressReleaseDto pressRelease,
        List<String> comments
) {
    public record PressReleaseDto(
            String title,
            String content,
            String imageUrl
    ) {
        public static PressReleaseDto from(PressReleaseDoc pressRelease) {
            return new PressReleaseDto(
                    pressRelease.title(),
                    pressRelease.content(),
                    pressRelease.imaUrl()
            );
        }
    }
}
