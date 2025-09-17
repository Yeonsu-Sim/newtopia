package io.ssafy.p.i13c203.gameserver.domain.game.dto.response;

import io.ssafy.p.i13c203.gameserver.domain.game.dto.CardBriefDto;
import io.ssafy.p.i13c203.gameserver.domain.game.dto.CountryStatsDto;
import io.ssafy.p.i13c203.gameserver.domain.game.dto.GameDto;
import io.ssafy.p.i13c203.gameserver.domain.game.dto.TurnDetailDto;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;

public record GameDetailResponse(
        GameDto game
) {
    public static GameDetailResponse from(Game g) {
        return new GameDetailResponse(
                new GameDto(
                        g.getId(),
                        g.getCountryName(),
                        new TurnDetailDto(
                                g.getTurn(),
                                CountryStatsDto.of(g.getCountryStats()),
                                CardBriefDto.from(g.getCurrentCard())
                        )
                )
        );
    }
}