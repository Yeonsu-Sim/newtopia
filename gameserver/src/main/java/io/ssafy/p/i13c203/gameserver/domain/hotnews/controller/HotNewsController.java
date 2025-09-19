package io.ssafy.p.i13c203.gameserver.domain.hotnews.controller;

import io.ssafy.p.i13c203.gameserver.domain.hotnews.dto.HotNewsDto;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "핫 뉴스 API", description = "최근 핫 뉴스 제공 API")
public interface HotNewsController {

  @Operation(
    summary = "최신 핫뉴스 조회",
    description = "최신 순으로 정렬된 핫뉴스를 지정된 개수만큼 조회합니다."
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "핫뉴스 조회 성공",
      content = @Content(schema = @Schema(implementation = APIResponse.class))
    ),
    @ApiResponse(
      responseCode = "400",
      description = "잘못된 요청 파라미터"
    ),
    @ApiResponse(
      responseCode = "500",
      description = "서버 내부 오류"
    )
  })
  ResponseEntity<APIResponse<List<HotNewsDto>, Void>> getRecentNHotNews(
    @Parameter(
      description = "조회할 뉴스 개수 (기본값: 20)",
      example = "20",
      schema = @Schema(minimum = "1")
    )
    Integer limit
  );
}
