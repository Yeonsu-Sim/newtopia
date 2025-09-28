package io.ssafy.p.i13c203.gameserver.domain.admin.controller;

import io.ssafy.p.i13c203.gameserver.domain.notice.dto.response.NoticeResponse;
import io.ssafy.p.i13c203.gameserver.domain.notice.entity.Notice;
import io.ssafy.p.i13c203.gameserver.domain.notice.entity.NoticeType;
import io.ssafy.p.i13c203.gameserver.domain.notice.service.NoticeService;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/admin/notices")
@Tag(name = "Admin Notice API", description = "관리자용 공지사항 관리 API")
public class AdminNoticeController {

    private final NoticeService noticeService;

    @GetMapping("/all")
    @Operation(
        summary = "관리자용 모든 공지사항 조회",
        description = "관리자가 모든 공지사항을 조회합니다"
    )
    public ResponseEntity<APIResponse<List<NoticeResponse>, Void>> getAllNoticesForAdmin() {
        List<Notice> notices = noticeService.getAllNotices();
        List<NoticeResponse> response = notices.stream()
                .map(NoticeResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(APIResponse.success("공지사항 목록 조회 완료", response));
    }

    @GetMapping("/stats")
    @Operation(
        summary = "공지사항 통계",
        description = "타입별 공지사항 개수 통계"
    )
    public ResponseEntity<APIResponse<Map<String, Object>, Void>> getNoticeStats() {
        List<Notice> allNotices = noticeService.getAllNotices();

        Map<NoticeType, Long> typeStats = allNotices.stream()
                .collect(Collectors.groupingBy(Notice::getType, Collectors.counting()));

        Map<String, Object> stats = Map.of(
            "totalCount", allNotices.size(),
            "typeStats", typeStats,
            "recentCount", allNotices.stream()
                .filter(notice -> notice.getCreatedAt().isAfter(
                    java.time.LocalDateTime.now().minusDays(7)))
                .count()
        );

        return ResponseEntity.ok(APIResponse.success("공지사항 통계 조회 완료", stats));
    }
}