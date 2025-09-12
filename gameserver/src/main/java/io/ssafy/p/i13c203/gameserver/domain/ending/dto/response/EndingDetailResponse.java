package io.ssafy.p.i13c203.gameserver.domain.ending.dto.response;

import io.ssafy.p.i13c203.gameserver.domain.ending.doc.EndingDoc;
import io.ssafy.p.i13c203.gameserver.domain.ending.dto.EndingAssetsDto;

public record EndingDetailResponse(
        String code,
        String title,
        String content,
        EndingAssetsDto assets
) {
    public static EndingDetailResponse from(EndingDoc doc, EndingAssetsDto assets) {
        return new EndingDetailResponse(
                doc.code(),
                doc.title(),
                doc.content(),
                assets
        );
    }
}
