package io.ssafy.p.i13c203.gameserver.domain.gameresult.controller;

import io.ssafy.p.i13c203.gameserver.auth.security.CustomUserDetails;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.request.DetailQuery;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.request.ReportQuery;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.response.GameResultDetailResponse;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.response.GameResultReportResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "게임 결과 리포트 API",
        description = "게임 결과 리포트 조회 및 드릴다운 상세 조회 API"
)
@RequestMapping("/api/v1/game-results")
public interface GameResultController {

    @Operation(
            summary = "게임 결과 리포트 조회",
            description = """
                    특정 게임의 결과 리포트를 조회합니다.
                    parts로 context/summary/graph 일부만 선택할 수 있으며,
                    metrics/page/size/sort는 graph 섹션에 적용됩니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리포트 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "게임을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 쿼리 파라미터")
    })
    @GetMapping("/{gameId}/report")
    ResponseEntity<APIResponse<GameResultReportResponse, Void>> getReport(
            @Parameter(description = "게임 ID", example = "123")
            @PathVariable Long gameId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ParameterObject @Valid ReportQuery query
    );

    @Operation(
            summary = "게임 결과 드릴다운 상세 조회",
            description = """
                    타임라인의 특정 턴 상세(change before/after/delta, 카드/선택지/관련기사)를 조회합니다.
                    metrics를 지정하면 해당 지표만 반환합니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "드릴다운 상세 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "게임 또는 턴을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 쿼리 파라미터")
    })
    @GetMapping("/{gameId}/turns/{turnNumber}")
    ResponseEntity<APIResponse<GameResultDetailResponse, Void>> getTurnDetail(
            @Parameter(description = "게임 ID", example = "123")
            @PathVariable Long gameId,
            @Parameter(
                    name = "turnNumber",
                    description = "상세 조회할 턴 번호(1~finalTurnNumber)",
                    in = ParameterIn.PATH,
                    schema = @Schema(type = "integer", example = "7")
            )
            @PathVariable Integer turnNumber,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ParameterObject @Valid DetailQuery query
    );
}
