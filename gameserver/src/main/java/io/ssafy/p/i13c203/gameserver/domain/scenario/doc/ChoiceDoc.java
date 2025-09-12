package io.ssafy.p.i13c203.gameserver.domain.scenario.doc;

import io.ssafy.p.i13c203.gameserver.domain.game.doc.EffectDoc;

public record ChoiceDoc(
        String code,
        String label,
        EffectDoc effect
) {}