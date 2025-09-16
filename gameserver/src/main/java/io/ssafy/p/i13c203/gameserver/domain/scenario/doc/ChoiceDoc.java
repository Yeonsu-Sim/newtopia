package io.ssafy.p.i13c203.gameserver.domain.scenario.doc;

import io.ssafy.p.i13c203.gameserver.domain.game.doc.EffectDoc;
import java.util.*;

// TODO 여기에 ㄱㄱ  .
public record ChoiceDoc(
        String code,
        String label,
        EffectDoc effect,
        PressReleaseDoc pressRelease,
        List<String> comments
) {}