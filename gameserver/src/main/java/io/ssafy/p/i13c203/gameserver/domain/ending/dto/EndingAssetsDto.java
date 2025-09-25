package io.ssafy.p.i13c203.gameserver.domain.ending.dto;

import io.ssafy.p.i13c203.gameserver.domain.ending.entity.Ending;

public record EndingAssetsDto(
        String imageUrl,
        String thumbnailUrl
) {
    public static EndingAssetsDto from(Ending ending) {
        if (ending.getImage() == null) {
            return new EndingAssetsDto(null, null);
        }

        String imageUrl = ending.getImage().getUrl();
        if (imageUrl == null || imageUrl.isBlank()) {
            return new EndingAssetsDto(null, null);
        }

        String thumbnailUrl = imageUrl;  // 아직 미정
        return new EndingAssetsDto(imageUrl, thumbnailUrl);
    }
}