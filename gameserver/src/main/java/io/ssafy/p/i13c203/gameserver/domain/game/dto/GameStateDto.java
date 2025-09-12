package io.ssafy.p.i13c203.gameserver.domain.game.dto;

public record GameStateDto(
        boolean gameOver,
        Long gameResultId,
        EndingDto ending
) {
    public static GameStateDto of(boolean over, Long resultId, EndingDto ending) {
        return new GameStateDto(over, resultId, ending);
    }
}
