package io.ssafy.p.i13c203.gameserver.domain.suggestion.controller;

import io.ssafy.p.i13c203.gameserver.auth.security.CustomUserDetails;
import io.ssafy.p.i13c203.gameserver.domain.member.entity.Member;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.dto.request.SuggestionCreateRequest;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.dto.request.SuggestionRequest;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.dto.response.SuggestionCreateResponse;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.dto.response.SuggestionListResponse;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.dto.response.SuggestionResponse;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.entity.SuggestionCategory;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.repository.SuggestionRepository;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.service.SuggestionService;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Tag(name = "건의사항 API")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/suggestions")
public class SuggestionController {

    private final SuggestionService suggestionService;

    @Operation(
            summary = "건의사항 생성",
            description = "새로운 건의사항을 생성합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "건의사항 생성 예시",
                                    value = """
                                            {
                                              "title": "게임 UI 개선 건의",
                                              "text": "메인 화면의 버튼이 너무 작아서 클릭하기 어렵습니다. 버튼 크기를 조금 더 크게 만들어주세요.",
                                              "suggestionCategory": "UI",
                                              "imageIds": []
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "건의사항 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "message": "success",
                                              "data": {
                                                "suggestionId": 123
                                              },
                                              "error": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping
    public ResponseEntity<APIResponse<SuggestionCreateResponse, Void>> createSuggestion(
            @Valid @RequestBody SuggestionCreateRequest suggestionCreateRequest,
            @AuthenticationPrincipal CustomUserDetails details
            ){
        Member member = details.getMember();

        SuggestionCreateResponse response = suggestionService.createSuggestion(suggestionCreateRequest, member);

        return ResponseEntity.ok(
                APIResponse.success(response)
        );

    }


    // 보류
//    @GetMapping
//    public ResponseEntity<APIResponse<?, Void>> getSuggestion(
//            @RequestBody SuggestionRequest request
//    ){
//        List<SuggestionResponse> suggestion = suggestionService.getSuggestion(
//                request
//        );
//
//        SuggestionListResponse response = SuggestionListResponse.builder()
//                .list(suggestion)
//                .build();
//
//        return ResponseEntity.ok(
//                APIResponse.success(response)
//        );
//    }


    @Operation(
            summary = "건의사항 카테고리 목록 조회",
            description = "건의사항 작성 시 선택할 수 있는 카테고리 목록을 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "카테고리 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "카테고리 목록 응답 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "message": "success",
                                              "data": ["UI", "IN_GAME", "SCENARIO", "SERVER", "ETC"],
                                              "error": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/categories")
    public ResponseEntity<APIResponse<List<SuggestionCategory>, Void>> getCategories() {
        List<SuggestionCategory> categories = Arrays.asList(SuggestionCategory.values());
        return ResponseEntity.ok(APIResponse.success(categories));
    }

}
