package io.ssafy.p.i13c203.gameserver.domain.game.dto;


public record ChoiceHintDTO(Choice A, Choice B) {
    public record Choice(
            String economy,
            String defense,
            String environment,
            String publicSentiment
    ) {}
}
