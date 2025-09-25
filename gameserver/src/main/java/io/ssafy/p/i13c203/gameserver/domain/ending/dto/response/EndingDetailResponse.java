package io.ssafy.p.i13c203.gameserver.domain.ending.dto.response;

import io.ssafy.p.i13c203.gameserver.domain.ending.dto.EndingAssetsDto;
import io.ssafy.p.i13c203.gameserver.domain.ending.entity.Ending;

public record EndingDetailResponse(
        String code,
        String title,
        String content,
        EndingAssetsDto assets
) {
    public static EndingDetailResponse from(Ending ending, EndingAssetsDto assets) {
        return new EndingDetailResponse(
                ending.getCode(),
                ending.getTitle(),
                ending.getContent(),
                assets
        );
    }
}
