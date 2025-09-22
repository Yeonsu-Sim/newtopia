package io.ssafy.p.i13c203.gameserver.domain.suggestion.dto.response;

import io.ssafy.p.i13c203.gameserver.domain.suggestion.entity.SuggestionCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class SuggestionResponse {
    private String title;
    private String text;
    private SuggestionCategory category;
    private LocalDateTime createdAt;

}
