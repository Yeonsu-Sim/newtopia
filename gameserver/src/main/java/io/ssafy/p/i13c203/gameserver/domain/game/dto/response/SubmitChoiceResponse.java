package io.ssafy.p.i13c203.gameserver.domain.game.dto.response;

import io.ssafy.p.i13c203.gameserver.domain.game.dto.*;

public record SubmitChoiceResponse(
        AppliedDto applied,
        GameStateDto gameState,
        NextTurnDto nextTurn
) {
    public static SubmitChoiceResponse of(AppliedDto a, GameStateDto s, NextTurnDto n) {
        return new SubmitChoiceResponse(a, s, n);
    }
}

