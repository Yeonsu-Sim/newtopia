package io.ssafy.p.i13c203.gameserver.domain.scenario.doc;

import io.ssafy.p.i13c203.gameserver.domain.game.doc.ConditionEntryDoc;

import java.util.List;

public record SpawnConditionsDoc(
        List<ConditionEntryDoc> conditions
) {}