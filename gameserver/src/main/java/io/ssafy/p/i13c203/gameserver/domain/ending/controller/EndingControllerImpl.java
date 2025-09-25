package io.ssafy.p.i13c203.gameserver.domain.ending.controller;

import io.ssafy.p.i13c203.gameserver.auth.security.CustomUserDetails;
import io.ssafy.p.i13c203.gameserver.domain.ending.dto.EndingAssetsDto;
import io.ssafy.p.i13c203.gameserver.domain.ending.dto.response.EndingDetailResponse;
import io.ssafy.p.i13c203.gameserver.domain.ending.dto.response.GetMyEndingsResponse;
import io.ssafy.p.i13c203.gameserver.domain.ending.entity.Ending;
import io.ssafy.p.i13c203.gameserver.domain.ending.service.EndingService;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/endings")
@RequiredArgsConstructor
public class EndingControllerImpl implements EndingController {

    private final EndingService endingService;

    @Override
    public ResponseEntity<APIResponse<EndingDetailResponse, Void>> getEndingDetail(
            @PathVariable String endingCode) {
        Ending ending = endingService.getByCode(endingCode);

        EndingAssetsDto assets = EndingAssetsDto.from(ending);
        EndingDetailResponse body = EndingDetailResponse.from(ending, assets);

        return ResponseEntity.ok(APIResponse.success(null, body));
    }

    @Override
    public ResponseEntity<APIResponse<GetMyEndingsResponse, Void>> getMyEndings(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(APIResponse.fail("AUTH_REQUIRED", "인증이 필요합니다"));
        }
        Long memberId = userDetails.getMemberId();

        var response = endingService.getMyEndings(memberId);

        return ResponseEntity.ok(APIResponse.success(
                "수집한 엔딩을 조회했습니다", response));
    }

}
