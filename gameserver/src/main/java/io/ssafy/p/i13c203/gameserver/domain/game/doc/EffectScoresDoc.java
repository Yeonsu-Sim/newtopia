package io.ssafy.p.i13c203.gameserver.domain.game.doc;

import io.ssafy.p.i13c203.gameserver.domain.scenario.model.EffectApplyType;

/*
    대분류 점수
 */
public record EffectScoresDoc(
        EffectApplyType applyType,
        int economy,
        int defense,
        int publicSentiment,
        int environment
) {
    public EffectScoresDoc {
        if (applyType == null) {
            applyType = EffectApplyType.RELATIVE;  // 기본값
        }
    }
}