package io.ssafy.p.i13c203.gameserver.domain.game.dto;

import io.ssafy.p.i13c203.gameserver.domain.ending.doc.EndingDoc;

public record EndingDto(
        String code
) {
    public static EndingDto from(EndingDoc ending) {
        if (ending == null) {
            return null;
        }
        return new EndingDto(ending.code());
    }
}
