package io.ssafy.p.i13c203.gameserver.domain.game.doc;

/*
    대분류 점수
 */
public record EffectScoresDoc(
        int economy,
        int defense,
        int publicSentiment,
        int environment
) {}