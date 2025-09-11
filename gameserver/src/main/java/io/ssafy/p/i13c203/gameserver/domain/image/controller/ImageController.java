package io.ssafy.p.i13c203.gameserver.domain.image.controller;

import io.ssafy.p.i13c203.gameserver.domain.image.dto.response.ImageUploadResponse;
import io.ssafy.p.i13c203.gameserver.domain.image.entity.ImageStatus;
import io.ssafy.p.i13c203.gameserver.domain.image.service.ImageService;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse<ImageUploadResponse, Void>> upload(
            @RequestPart("file")MultipartFile file,
            @RequestParam(name = "domain", defaultValue = "suggestion") String domain,
            @RequestParam Long memberId
            ) throws IOException {

        String contentType = file.getContentType();
        Set<String> allowed = Set.of("image/png", "image/jpeg", "image/gif", "image/webp");

        // contentType 이 null 이면 거부

        ImageUploadResponse upload = imageService.upload(file, memberId, domain);


        return ResponseEntity.ok(
                APIResponse.success(upload)
        );

    }

    // 미구현
    // 가서 지우기 하면 됌
    @DeleteMapping("/{imageId}")
    public void delete(){

    }


}
