package io.ssafy.p.i13c203.gameserver.domain.ending.controller;

import io.ssafy.p.i13c203.gameserver.domain.ending.doc.EndingDoc;
import io.ssafy.p.i13c203.gameserver.domain.ending.dto.EndingAssetsDto;
import io.ssafy.p.i13c203.gameserver.domain.ending.dto.response.EndingDetailResponse;
import io.ssafy.p.i13c203.gameserver.domain.ending.service.EndingService;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/endings")
@RequiredArgsConstructor
public class EndingControllerImpl implements EndingController {

    private final EndingService endingService;

    @Override
    public ResponseEntity<APIResponse<EndingDetailResponse, Void>> getEndingDetail(
            @PathVariable String endingCode) {
        EndingDoc doc = endingService.getByCode(endingCode);

        EndingAssetsDto assets = buildAssets(doc);
        EndingDetailResponse body = EndingDetailResponse.from(doc, assets);

        return ResponseEntity.ok(APIResponse.success(null, body));
    }

    private EndingAssetsDto buildAssets(EndingDoc doc) {
        String imageUrl = doc.imageUrl();
        if (imageUrl == null || imageUrl.isBlank()) {
            return new EndingAssetsDto(null, null);
        }

        String thumbnailUrl = imageUrl;  // 아직 미정
        return new EndingAssetsDto(imageUrl, thumbnailUrl);
    }
}
