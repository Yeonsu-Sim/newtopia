package io.ssafy.p.i13c203.gameserver.domain.game.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SubmitChoiceRequest(
        @NotNull(message = "cardId가 필요합니다.")
        UUID cardId,

        @NotBlank(message = "choice가 필요합니다.")
        String choice
) {}
