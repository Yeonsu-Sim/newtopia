package io.ssafy.p.i13c203.gameserver.domain.ending.doc;

import io.ssafy.p.i13c203.gameserver.domain.ending.entity.Ending;
import io.ssafy.p.i13c203.gameserver.domain.image.entity.Image;

public record EndingDoc(
        String code,
        String title,
        String content,
        String condition,    // "economy==100" 등 (표시/로그용)
        String imageUrl    // 이미지 URL
) {
    public static EndingDoc from(Ending ending) {
        if (ending == null) {
            return null;
        }

        Image img = ending.getImage();
        String imageUrl = (img != null) ? img.getUrl() : null;

        return new EndingDoc(
                ending.getCode(),
                ending.getTitle(),
                ending.getContent(),
                ending.getCondition(),
                imageUrl
        );
    }
}