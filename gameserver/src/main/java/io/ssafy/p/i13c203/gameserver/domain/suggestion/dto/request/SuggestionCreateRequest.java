package io.ssafy.p.i13c203.gameserver.domain.suggestion.dto.request;

import io.ssafy.p.i13c203.gameserver.domain.suggestion.entity.SuggestionCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SuggestionCreateRequest {

    @NotBlank(message = "title이 null 일 수 없습니다.")
    private String title;

    @NotBlank(message = "text가 null일 수 없습니다.")
    private String text;

    @NotNull
    private SuggestionCategory suggestionCategory;

    private int[] imageIds;

}
