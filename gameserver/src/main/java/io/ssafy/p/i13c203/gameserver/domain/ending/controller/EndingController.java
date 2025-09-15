package io.ssafy.p.i13c203.gameserver.domain.ending.controller;

import io.ssafy.p.i13c203.gameserver.domain.ending.dto.response.EndingDetailResponse;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "엔딩 API", description = "게임 엔딩 조회 관련 API")
public interface EndingController {

    @Operation(
        summary = "엔딩 상세 정보 조회",
        description = "엔딩 코드로 해당 엔딩의 상세 정보와 에셋(이미지, 썸네일)을 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "엔딩 정보 조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 엔딩 코드"),
        @ApiResponse(responseCode = "400", description = "잘못된 엔딩 코드 형식")
    })
    @GetMapping("/{endingCode}")
    ResponseEntity<APIResponse<EndingDetailResponse, Void>> getEndingDetail(
            @Parameter(description = "엔딩 코드", example = "ECONOMY_HIGH", required = true)
            @PathVariable String endingCode
    );
}