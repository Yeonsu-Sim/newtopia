package io.ssafy.p.i13c203.gameserver.domain.gameresult.controller;

import io.ssafy.p.i13c203.gameserver.auth.security.CustomUserDetails;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.request.DetailQuery;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.request.ReportQuery;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.response.GameResultDetailResponse;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.response.GameResultReportResponse;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.service.GameResultService;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GameResultControllerImpl implements GameResultController {

    private final GameResultService gameResultService;

    @Override
    public ResponseEntity<APIResponse<GameResultReportResponse, Void>> getReport(
            Long gameId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ParameterObject @Valid ReportQuery query
    ) {
        // parts 미지정 시 전체 확장은 서비스에서 처리(권장)
        var result = gameResultService.getReport(
                userDetails.getMemberId(),  // or getId()
                gameId,
                query
        );
        return ResponseEntity.ok(APIResponse.success("리포트를 불러왔습니다.", result));
    }

    @Override
    public ResponseEntity<APIResponse<GameResultDetailResponse, Void>> getTurnDetail(
            Long gameId,
            Integer turnNumber,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ParameterObject @Valid DetailQuery query
    ) {
        var detail = gameResultService.getTurnDetail(
                userDetails.getMemberId(),
                gameId,
                turnNumber,
                query
        );
        return ResponseEntity.ok(APIResponse.success("드릴다운 상세를 불러왔습니다.", detail));
    }
}
