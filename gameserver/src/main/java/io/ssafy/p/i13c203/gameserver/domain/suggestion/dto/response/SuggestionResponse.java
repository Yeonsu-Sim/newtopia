package io.ssafy.p.i13c203.gameserver.domain.suggestion.dto.response;

import io.ssafy.p.i13c203.gameserver.domain.suggestion.entity.Suggestion;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.entity.SuggestionCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class SuggestionResponse {
    private Long id;
    private String title;
    private String text;
    private SuggestionCategory category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SuggestionResponse from(Suggestion suggestion) {
        return SuggestionResponse.builder()
                .id(suggestion.getId())
                .title(suggestion.getTitle())
                .text(suggestion.getText())
                .category(suggestion.getCategory())
                .createdAt(suggestion.getCreatedAt())
                .updatedAt(suggestion.getUpdatedAt())
                .build();
    }
}
