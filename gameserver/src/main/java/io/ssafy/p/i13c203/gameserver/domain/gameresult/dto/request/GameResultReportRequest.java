package io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GameResultReportRequest {
    private List<String> parts;   // ["context","summary","graph"]
    private List<String> metrics; // ["economy","defense",...]
    private Integer page;         // ≥1, default 1
    private Integer size;         // 1~200, default 12
    private String sort;          // "asc" | "desc"
}
