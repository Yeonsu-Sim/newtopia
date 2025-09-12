package io.ssafy.p.i13c203.gameserver.domain.game.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateGameRequest(
        @NotBlank(message = "나라이름을 입력하세요")
        @Size(min = 1, max = 32, message = "나라이름은 1글자 이상 32자 이하이어야 합니다")
        String countryName
) {}
