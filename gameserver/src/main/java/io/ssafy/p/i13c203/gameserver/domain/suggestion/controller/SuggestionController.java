package io.ssafy.p.i13c203.gameserver.domain.suggestion.controller;

import io.ssafy.p.i13c203.gameserver.domain.suggestion.dto.request.SuggestionCreateRequest;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.dto.request.SuggestionRequest;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.dto.response.SuggestionCreateResponse;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.dto.response.SuggestionListResponse;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.dto.response.SuggestionResponse;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.repository.SuggestionRepository;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.service.SuggestionService;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/suggestions")
public class SuggestionController {

    private final SuggestionService suggestionService;

    @PostMapping
    public ResponseEntity<APIResponse<SuggestionCreateResponse, Void>> createSuggestion(
            @Valid @RequestBody SuggestionCreateRequest suggestionCreateRequest
            ){

        SuggestionCreateResponse response = suggestionService.createSuggestion(suggestionCreateRequest);

        return ResponseEntity.ok(
                APIResponse.success(response)
        );

    }


    // 보류
    @GetMapping
    public ResponseEntity<APIResponse<?, Void>> getSuggestion(
            @RequestBody SuggestionRequest request
    ){
        List<SuggestionResponse> suggestion = suggestionService.getSuggestion(
                request
        );

        SuggestionListResponse response = SuggestionListResponse.builder()
                .list(suggestion)
                .build();

        return ResponseEntity.ok(
                APIResponse.success(response)
        );
    }

}
