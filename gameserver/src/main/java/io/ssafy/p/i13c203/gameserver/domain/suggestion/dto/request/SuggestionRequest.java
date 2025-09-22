package io.ssafy.p.i13c203.gameserver.domain.suggestion.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SuggestionRequest {
    private Long memberId;
}
