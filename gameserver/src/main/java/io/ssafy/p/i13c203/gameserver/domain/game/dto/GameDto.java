package io.ssafy.p.i13c203.gameserver.domain.game.dto;

public record GameDto(
        Long gameId,
        String countryName,
        TurnDetailDto turn
) {}
