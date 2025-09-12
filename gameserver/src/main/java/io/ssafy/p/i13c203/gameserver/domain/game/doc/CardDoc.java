package io.ssafy.p.i13c203.gameserver.domain.game.doc;

import io.ssafy.p.i13c203.gameserver.domain.game.model.CardType;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.ChoiceDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.NpcRefDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.RelatedArticleDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.SpawnConditionsDoc;

import java.util.Map;
import java.util.UUID;

public record CardDoc(
        UUID cardId,
        Long scenarioId,
        CardType type,
        String title,
        String content,
        NpcRefDoc npc,
        SpawnConditionsDoc spawn,
        Map<String, ChoiceDoc> choices,
        RelatedArticleDoc relatedArticle
) {}

