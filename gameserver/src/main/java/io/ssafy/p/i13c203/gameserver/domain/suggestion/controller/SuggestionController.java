package io.ssafy.p.i13c203.gameserver.domain.suggestion.controller;

import io.ssafy.p.i13c203.gameserver.domain.suggestion.dto.request.SuggestionCreateRequest;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/suggestions")
public class SuggestionController {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse<?, Void>> createSuggestion(
            @RequestPart("dto") @Valid SuggestionCreateRequest dto,
            @RequestPart(value = "image", required = false) MultipartFile image
            ){

        log.info("suggestion request : {}", dto);




        return null;
    }

}
