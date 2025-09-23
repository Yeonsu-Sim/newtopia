package io.ssafy.p.i13c203.gameserver.domain.notice.controller;

import io.ssafy.p.i13c203.gameserver.auth.security.CustomUserDetails;
import io.ssafy.p.i13c203.gameserver.domain.notice.dto.request.CreateNoticeRequest;
import io.ssafy.p.i13c203.gameserver.domain.notice.dto.response.NoticeResponse;
import io.ssafy.p.i13c203.gameserver.domain.notice.entity.Notice;
import io.ssafy.p.i13c203.gameserver.domain.notice.entity.NoticeType;
import io.ssafy.p.i13c203.gameserver.domain.notice.service.NoticeService;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notices")
@Slf4j
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    public ResponseEntity<APIResponse<NoticeResponse, Void>> createNotice(
            @AuthenticationPrincipal CustomUserDetails details,
            @RequestBody CreateNoticeRequest request) {

        Notice notice = noticeService.createNotice(
                details.getMember(),
                request.getTitle(),
                request.getContent(),
                request.getType()
        );

        return ResponseEntity.ok(APIResponse.success(NoticeResponse.from(notice)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<NoticeResponse, Void>> getNotice(@PathVariable Long id) {
        Optional<Notice> notice = noticeService.getNoticeById(id);

        if (notice.isPresent()) {
            return ResponseEntity.ok(APIResponse.success(NoticeResponse.from(notice.get())));
        } else {
            return ResponseEntity.status(404).body(APIResponse.fail("NOT_FOUND", "공지사항을 찾을 수 없습니다."));
        }
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<NoticeResponse>, Void>> getAllNotices() {
        List<Notice> notices = noticeService.getAllNotices();
        List<NoticeResponse> response = notices.stream()
                .map(NoticeResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(APIResponse.success(response));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<APIResponse<List<NoticeResponse>, Void>> getNoticesByType(@PathVariable NoticeType type) {
        List<Notice> notices = noticeService.getNoticesByType(type);
        List<NoticeResponse> response = notices.stream()
                .map(NoticeResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(APIResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void, Void>> deleteNotice(@PathVariable Long id) {
        try {
            noticeService.deleteNotice(id);
            return ResponseEntity.ok(APIResponse.success("공지사항이 삭제되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(APIResponse.fail("NOT_FOUND", "삭제할 공지사항을 찾을 수 없습니다."));
        }
    }
}