package io.ssafy.p.i13c203.gameserver.domain.notice.controller;

import io.ssafy.p.i13c203.gameserver.domain.notice.dto.request.CreateNoticeRequest;
import io.ssafy.p.i13c203.gameserver.domain.notice.dto.response.NoticeResponse;
import io.ssafy.p.i13c203.gameserver.domain.notice.entity.Notice;
import io.ssafy.p.i13c203.gameserver.domain.notice.entity.NoticeType;
import io.ssafy.p.i13c203.gameserver.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<NoticeResponse> createNotice(
            @RequestParam Long memberId,
            @RequestBody CreateNoticeRequest request) {

        Notice notice = noticeService.createNotice(
                memberId,
                request.getTitle(),
                request.getContent(),
                request.getType()
        );

        return ResponseEntity.ok(NoticeResponse.from(notice));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponse> getNotice(@PathVariable Long id) {
        Optional<Notice> notice = noticeService.getNoticeById(id);

        if (notice.isPresent()) {
            return ResponseEntity.ok(NoticeResponse.from(notice.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<NoticeResponse>> getAllNotices(Pageable pageable) {
        Page<Notice> notices = noticeService.getAllNotices(pageable);
        Page<NoticeResponse> response = notices.map(NoticeResponse::from);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<NoticeResponse>> getNoticesByType(@PathVariable NoticeType type) {
        List<Notice> notices = noticeService.getNoticesByType(type);
        List<NoticeResponse> response = notices.stream()
                .map(NoticeResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        try {
            noticeService.deleteNotice(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}