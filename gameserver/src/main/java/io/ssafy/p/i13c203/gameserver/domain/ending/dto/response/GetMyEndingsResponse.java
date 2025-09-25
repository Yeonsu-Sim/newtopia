package io.ssafy.p.i13c203.gameserver.domain.ending.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.ssafy.p.i13c203.gameserver.domain.ending.dto.EndingAssetsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "내 엔딩 수집 현황 응답 DTO")
public class GetMyEndingsResponse {

    private Summary summary;
    private List<Ending> endings;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "엔딩 수집 현황 요약")
    public static class Summary {
        @Schema(description = "전체 엔딩 개수", example = "11")
        private int total;

        @Schema(description = "내가 수집한 엔딩 개수", example = "2")
        private int collected;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "엔딩 상세 정보")
    public static class Ending {
        @Schema(description = "엔딩 코드", example = "ECO_MAX")
        private String code;

        @Schema(description = "엔딩 제목", example = "경제가 100이 되었습니다.")
        private String title;

        @Schema(description = "엔딩 내용", example = "부는 쌓였지만, 나눌 생각은 없었다.")
        private String content;

        private EndingAssetsDto assets;

        private EndingStatus status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "엔딩 수집 상태")
    public static class EndingStatus {
        @Schema(description = "수집 여부", example = "true")
        private boolean collected;

        @Schema(description = "수집 횟수", example = "5")
        private int count;

        @Schema(description = "가장 최근에 수집한 시각", example = "2025-09-24T10:15:30")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime lastCollectedAt;
    }

}