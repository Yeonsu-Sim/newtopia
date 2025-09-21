package io.ssafy.p.i13c203.gameserver.domain.image.controller;

import io.ssafy.p.i13c203.gameserver.domain.image.dto.response.ImageUploadResponse;
import io.ssafy.p.i13c203.gameserver.domain.image.entity.ImageStatus;
import io.ssafy.p.i13c203.gameserver.domain.image.service.ImageService;
import io.ssafy.p.i13c203.gameserver.auth.security.CustomUserDetails;
import io.ssafy.p.i13c203.gameserver.domain.image.dto.response.ImageUploadResponse;
import io.ssafy.p.i13c203.gameserver.domain.image.entity.ImageStatus;
import io.ssafy.p.i13c203.gameserver.domain.image.service.ImageService;
import io.ssafy.p.i13c203.gameserver.domain.member.entity.Member;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
            @AuthenticationPrincipal CustomUserDetails details
            ) throws IOException {

        String contentType = file.getContentType();
        Set<String> allowed = Set.of("image/png", "image/jpeg", "image/gif", "image/webp");

        // contentType 이 null 이면 거부


        Member member = details.getMember();

        ImageUploadResponse upload = imageService.upload(file, member, domain);


        return ResponseEntity.ok(
                APIResponse.success(upload)
        );

    }


    // api/v1/files/public
    @PostMapping(value = "/public", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse<ImageUploadResponse, Void>> uploadPublic(
            @RequestPart("file") MultipartFile file,
            @RequestParam(name = "domain", defaultValue = "public") String domain
    ) throws IOException {

        String contentType = file.getContentType();
        Set<String> allowed = Set.of("image/png", "image/jpeg", "image/gif", "image/webp");

        if (contentType == null || !allowed.contains(contentType)) {
            throw new IllegalArgumentException("Unsupported file type");
        }

        ImageUploadResponse upload = imageService.uploadPublic(file, domain);

        return ResponseEntity.ok(
                APIResponse.success(upload)
        );
    }

    // 건의사항 이미지 전용 업로드 (인증 불필요)
    @PostMapping(value = "/suggestion", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse<ImageUploadResponse, Void>> uploadForSuggestion(
            @RequestPart("file") MultipartFile file
    ) throws IOException {

        String contentType = file.getContentType();
        Set<String> allowed = Set.of("image/png", "image/jpeg", "image/gif", "image/webp");

        if (contentType == null || !allowed.contains(contentType)) {
            throw new IllegalArgumentException("Unsupported file type");
        }

        ImageUploadResponse upload = imageService.uploadPublic(file, "suggestion");

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
