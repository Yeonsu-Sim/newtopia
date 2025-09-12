package io.ssafy.p.i13c203.gameserver.domain.game.dto.response;

import io.ssafy.p.i13c203.gameserver.domain.game.dto.GameSummaryDto;

public record GetMyGameResponse(
        GameSummaryDto game
) {
    public static GetMyGameResponse of(GameSummaryDto game) {
        return new GetMyGameResponse(game);
    }
}
