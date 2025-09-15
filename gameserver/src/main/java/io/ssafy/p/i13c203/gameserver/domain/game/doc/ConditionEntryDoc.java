package io.ssafy.p.i13c203.gameserver.domain.game.doc;

import io.ssafy.p.i13c203.gameserver.domain.game.model.ConditionOperator;
import io.ssafy.p.i13c203.gameserver.domain.game.model.MinorCategory;

/*
    등장 조건: 중분류별 임계값과 연산자
 */
public record ConditionEntryDoc(
        MinorCategory category,
        ConditionOperator operator,
        double threshold
) {}
