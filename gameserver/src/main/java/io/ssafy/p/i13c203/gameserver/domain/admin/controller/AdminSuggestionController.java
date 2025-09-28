package io.ssafy.p.i13c203.gameserver.domain.admin.controller;

import io.ssafy.p.i13c203.gameserver.domain.suggestion.dto.response.SuggestionResponse;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.entity.Suggestion;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.entity.SuggestionCategory;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.repository.SuggestionRepository;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/admin/suggestions")
@Tag(name = "Admin Suggestion API", description = "관리자용 건의사항 관리 API")
public class AdminSuggestionController {

    private final SuggestionRepository suggestionRepository;

    @GetMapping("/all")
    @Operation(
        summary = "관리자용 모든 건의사항 조회",
        description = "관리자가 모든 건의사항을 페이징으로 조회합니다"
    )
    public ResponseEntity<APIResponse<Map<String, Object>, Void>> getAllSuggestionsForAdmin(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Suggestion> suggestionPage = suggestionRepository.findAll(pageable);

        List<SuggestionResponse> suggestions = suggestionPage.getContent().stream()
                .map(SuggestionResponse::from)
                .collect(Collectors.toList());

        Map<String, Object> response = Map.of(
            "suggestions", suggestions,
            "totalElements", suggestionPage.getTotalElements(),
            "totalPages", suggestionPage.getTotalPages(),
            "currentPage", page,
            "size", size
        );

        return ResponseEntity.ok(APIResponse.success("건의사항 목록 조회 완료", response));
    }

    @GetMapping("/category/{category}")
    @Operation(
        summary = "카테고리별 건의사항 조회",
        description = "특정 카테고리의 건의사항을 조회합니다"
    )
    public ResponseEntity<APIResponse<List<SuggestionResponse>, Void>> getSuggestionsByCategory(
            @Parameter(description = "건의사항 카테고리")
            @PathVariable SuggestionCategory category) {

        List<Suggestion> suggestions = suggestionRepository.findByCategory(category);
        List<SuggestionResponse> response = suggestions.stream()
                .map(SuggestionResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(APIResponse.success("카테고리별 건의사항 조회 완료", response));
    }

    @GetMapping("/stats")
    @Operation(
        summary = "건의사항 통계",
        description = "카테고리별 건의사항 개수 통계"
    )
    public ResponseEntity<APIResponse<Map<String, Object>, Void>> getSuggestionStats() {
        List<Suggestion> allSuggestions = suggestionRepository.findAll();

        Map<SuggestionCategory, Long> categoryStats = allSuggestions.stream()
                .collect(Collectors.groupingBy(Suggestion::getCategory, Collectors.counting()));

        Map<String, Object> stats = Map.of(
            "totalCount", allSuggestions.size(),
            "categoryStats", categoryStats,
            "recentCount", allSuggestions.stream()
                .filter(suggestion -> suggestion.getCreatedAt().isAfter(
                    java.time.LocalDateTime.now().minusDays(7)))
                .count()
        );

        return ResponseEntity.ok(APIResponse.success("건의사항 통계 조회 완료", stats));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "특정 건의사항 상세 조회",
        description = "관리자가 특정 건의사항의 상세 정보를 조회합니다"
    )
    public ResponseEntity<APIResponse<SuggestionResponse, Object>> getSuggestionById(
            @Parameter(description = "건의사항 ID")
            @PathVariable Long id) {

        return suggestionRepository.findById(id)
                .map(suggestion -> ResponseEntity.ok(
                    APIResponse.success("건의사항 조회 완료", SuggestionResponse.from(suggestion))))
                .orElse(ResponseEntity.status(404).body(
                    APIResponse.fail("NOT_FOUND", "건의사항을 찾을 수 없습니다.")));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "건의사항 삭제",
        description = "관리자가 특정 건의사항을 삭제합니다"
    )
    public ResponseEntity<APIResponse<Void, Void>> deleteSuggestion(
            @Parameter(description = "삭제할 건의사항 ID")
            @PathVariable Long id) {

        if (suggestionRepository.existsById(id)) {
            suggestionRepository.deleteById(id);
            return ResponseEntity.ok(APIResponse.success("건의사항이 삭제되었습니다.", null));
        } else {
            return ResponseEntity.status(404).body(
                APIResponse.fail("NOT_FOUND", "삭제할 건의사항을 찾을 수 없습니다."));
        }
    }
}