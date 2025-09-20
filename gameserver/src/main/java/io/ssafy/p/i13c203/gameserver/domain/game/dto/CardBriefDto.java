package io.ssafy.p.i13c203.gameserver.domain.game.dto;

import io.ssafy.p.i13c203.gameserver.domain.game.doc.CardDoc;

import io.ssafy.p.i13c203.gameserver.domain.game.dto.ChoiceLabelDto.PressReleaseDto;
import java.util.List;
import java.util.UUID;

public record CardBriefDto(
        UUID cardId,
        String type,
        NpcDto npc,
        String content,
        List<ChoiceLabelDto> choices,
        RelatedArticleDto relatedArticle
) {
    public static CardBriefDto from(CardDoc c) {
        return new CardBriefDto(
                c.cardId(),
                c.type().name(),
                new NpcDto(c.npc().name(), c.npc().imageUrl()),
                c.content(),

                c.choices().entrySet().stream()
                        .map(e -> new ChoiceLabelDto(
                                e.getKey(),
                                e.getValue().label(),
                                PressReleaseDto.from(e.getValue().pressRelease()),
                                e.getValue().comments()
                            )
                        )
                        .toList(),

                c.relatedArticle() == null ? null : new RelatedArticleDto(
                        c.relatedArticle().title(),
                        c.relatedArticle().url(),
                        c.relatedArticle().content()
                )
        );
    }

    public record NpcDto(String name, String imageUrl) {}
    public record RelatedArticleDto(String title, String url, String content) {}
}

