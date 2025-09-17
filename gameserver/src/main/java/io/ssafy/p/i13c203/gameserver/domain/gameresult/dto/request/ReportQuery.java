package io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.request;

import io.ssafy.p.i13c203.gameserver.common.dto.request.SortDirection;
import jakarta.validation.constraints.Min;

import java.util.List;
import java.util.Set;

public record ReportQuery(

        Set<Part> parts,          // context | summary | graph (비지정 시 서비스에서 전부 확장)
        List<Metric> metrics,     // null/empty => 전부
        @Min(1)
        Integer page,             // 기본 1
        @Min(1)
        Integer size,             // 기본 12
        SortDirection sort        // 기본 asc
) {
        public ReportQuery {
                if (page == null) page = 1;
                if (size == null) size = 12;
                if (sort == null) sort = SortDirection.ASC;
        }
}
