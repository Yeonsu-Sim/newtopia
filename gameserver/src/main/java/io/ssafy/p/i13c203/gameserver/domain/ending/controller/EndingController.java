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
public class EndingController {

    private final EndingService endingService;

    @GetMapping("/{endingCode}")
    public ResponseEntity<APIResponse<EndingDetailResponse, Void>> getEndingDetail(
            @PathVariable String endingCode
                                                                                  ) {
        // EndingService 는 code 기반으로 조회(스위치/DB)하고, 못 찾으면 BusinessException(404 매핑)을 던지게 구성
        EndingDoc doc = endingService.getByCode(endingCode);

        EndingAssetsDto assets = buildAssets(doc);
        EndingDetailResponse body = EndingDetailResponse.from(doc, assets);

        return ResponseEntity.ok(APIResponse.success(null, body));
    }

    private EndingAssetsDto buildAssets(EndingDoc doc) {
        // 임시 규칙:
        //  - doc.endingS3Key() 가 "ending/economy_100.png" 처럼 온다고 가정
        //  - imageUrl  : {cdnBase}/{key} (배너/메인)
        //  - thumbnail : {cdnBase}/thumbs/{basename}.png  (아이콘/썸네일)
        String key = doc.endingS3Key();
        if (key == null || key.isBlank()) {
            return new EndingAssetsDto(null, null);
        }
        String imageUrl = "/" + key;
        String fileName = key.substring(key.lastIndexOf('/') + 1);
        String thumbnailUrl = "/thumbs/" + fileName; // 필요 시 규칙 변경
        return new EndingAssetsDto(imageUrl, thumbnailUrl);
    }
}
