package io.ssafy.p.i13c203.gameserver.domain.game.dto;

import io.ssafy.p.i13c203.gameserver.domain.ending.entity.Ending;

public record EndingDto(
        String code
) {
    public static EndingDto from(Ending ending) {
        if (ending == null) {
            return null;
        }
        return new EndingDto(ending.getCode());
    }
}
