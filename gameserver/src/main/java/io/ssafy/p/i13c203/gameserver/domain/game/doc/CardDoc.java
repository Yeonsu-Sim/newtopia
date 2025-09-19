package io.ssafy.p.i13c203.gameserver.domain.game.doc;

import io.ssafy.p.i13c203.gameserver.domain.game.model.CardType;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.ChoiceDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.NpcRefDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.RelatedArticleDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.SpawnConditionsDoc;

import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Npc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
import io.ssafy.p.i13c203.gameserver.global.exception.BusinessException;
import io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode;
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
) {
    public static CardDoc of(Scenario sc, CardType type) {
        Npc npc = sc.getNpc();
        if (npc == null) throw new BusinessException(ErrorCode.NPC_NOT_FOUND);

        NpcRefDoc npcRef = new NpcRefDoc(
                npc.getId(),           // Long (PK)
                npc.getName(),
                npc.getImage() != null ? npc.getImage().getUrl() : null
        );

        return new CardDoc(
                java.util.UUID.randomUUID(), // cardId: 런타임 UUID
                sc.getId(),                  // scenarioId: Long
                type,
                sc.getTitle(),
                sc.getContent(),
                npcRef,
                sc.getSpawn(),
                sc.getChoices(),
                sc.getRelatedArticle()
        );
    }
}