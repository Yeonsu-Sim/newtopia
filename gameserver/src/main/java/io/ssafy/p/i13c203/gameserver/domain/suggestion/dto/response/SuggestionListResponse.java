package io.ssafy.p.i13c203.gameserver.domain.suggestion.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@Builder
public class SuggestionListResponse {
    List<SuggestionResponse> list;
}
