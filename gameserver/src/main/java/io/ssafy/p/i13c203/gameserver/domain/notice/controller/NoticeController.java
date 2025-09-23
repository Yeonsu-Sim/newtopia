package io.ssafy.p.i13c203.gameserver.domain.notice.controller;

import io.ssafy.p.i13c203.gameserver.auth.security.CustomUserDetails;
import io.ssafy.p.i13c203.gameserver.domain.notice.dto.request.CreateNoticeRequest;
import io.ssafy.p.i13c203.gameserver.domain.notice.dto.response.NoticeResponse;
import io.ssafy.p.i13c203.gameserver.domain.notice.entity.Notice;
import io.ssafy.p.i13c203.gameserver.domain.notice.entity.NoticeType;
import io.ssafy.p.i13c203.gameserver.domain.notice.service.NoticeService;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Notice API", description = "공지사항 관리 API - 4가지 공지사항 타입(NOTICE: 일반공지, HOTFIX: 핫픽스, EVENT: 이벤트, UPDATE: 업데이트) 지원")
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    @Operation(
        summary = "공지사항 생성",
        description = "새로운 공지사항을 생성합니다. 4가지 타입을 지원합니다: NOTICE(일반공지), HOTFIX(핫픽스), EVENT(이벤트), UPDATE(업데이트)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "공지사항 생성 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = APIResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "success": true,
                        "message": "success",
                        "data": {
                            "id": 1,
                            "title": "중요 공지사항",
                            "content": "서버 점검 안내",
                            "type": "NOTICE",
                            "createdAt": "2023-12-01T10:00:00",
                            "updatedAt": "2023-12-01T10:00:00"
                        }
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<APIResponse<NoticeResponse, Void>> createNotice(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails details,
            @Parameter(
                description = "공지사항 생성 요청 데이터",
                required = true,
                content = @Content(
                    examples = @ExampleObject(
                        value = """
                        {
                            "title": "중요 공지사항",
                            "content": "서버 점검이 예정되어 있습니다.",
                            "type": "NOTICE"
                        }
                        """
                    )
                )
            ) @RequestBody CreateNoticeRequest request) {

        Notice notice = noticeService.createNotice(
                details.getMember(),
                request.getTitle(),
                request.getContent(),
                request.getType()
        );

        return ResponseEntity.ok(APIResponse.success(NoticeResponse.from(notice)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "특정 공지사항 조회",
        description = "ID로 특정 공지사항을 조회합니다"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "공지사항 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = APIResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "success": true,
                        "message": "success",
                        "data": {
                            "id": 1,
                            "title": "긴급 핫픽스",
                            "content": "버그 수정을 위한 긴급 패치입니다.",
                            "type": "HOTFIX",
                            "createdAt": "2023-12-01T10:00:00",
                            "updatedAt": "2023-12-01T10:00:00"
                        }
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "공지사항을 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                        "success": false,
                        "error": "NOT_FOUND",
                        "message": "공지사항을 찾을 수 없습니다."
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<APIResponse<NoticeResponse, Void>> getNotice(
            @Parameter(description = "공지사항 ID", example = "1") @PathVariable Long id) {
        Optional<Notice> notice = noticeService.getNoticeById(id);

        if (notice.isPresent()) {
            return ResponseEntity.ok(APIResponse.success(NoticeResponse.from(notice.get())));
        } else {
            return ResponseEntity.status(404).body(APIResponse.fail("NOT_FOUND", "공지사항을 찾을 수 없습니다."));
        }
    }

    @GetMapping
    @Operation(
        summary = "모든 공지사항 조회",
        description = "모든 공지사항을 조회합니다. 모든 타입(NOTICE, HOTFIX, EVENT, UPDATE)의 공지사항이 포함됩니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "공지사항 목록 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = APIResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "success": true,
                        "message": "success",
                        "data": [
                            {
                                "id": 1,
                                "title": "일반 공지사항",
                                "content": "서버 점검 안내",
                                "type": "NOTICE",
                                "createdAt": "2023-12-01T10:00:00",
                                "updatedAt": "2023-12-01T10:00:00"
                            },
                            {
                                "id": 2,
                                "title": "겨울 이벤트",
                                "content": "12월 특별 이벤트를 진행합니다.",
                                "type": "EVENT",
                                "createdAt": "2023-12-01T11:00:00",
                                "updatedAt": "2023-12-01T11:00:00"
                            }
                        ]
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<APIResponse<List<NoticeResponse>, Void>> getAllNotices() {
        List<Notice> notices = noticeService.getAllNotices();
        List<NoticeResponse> response = notices.stream()
                .map(NoticeResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(APIResponse.success(response));
    }

    @GetMapping("/type/{type}")
    @Operation(
        summary = "타입별 공지사항 조회",
        description = "특정 타입의 공지사항을 조회합니다. 지원되는 타입: NOTICE(일반공지), HOTFIX(핫픽스), EVENT(이벤트), UPDATE(업데이트)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "타입별 공지사항 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = APIResponse.class),
                examples = {
                    @ExampleObject(
                        name = "NOTICE 타입 예시",
                        value = """
                        {
                            "success": true,
                            "message": "success",
                            "data": [
                                {
                                    "id": 1,
                                    "title": "일반 공지사항",
                                    "content": "서버 점검 안내입니다.",
                                    "type": "NOTICE",
                                    "createdAt": "2023-12-01T10:00:00",
                                    "updatedAt": "2023-12-01T10:00:00"
                                }
                            ]
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "HOTFIX 타입 예시",
                        value = """
                        {
                            "success": true,
                            "message": "success",
                            "data": [
                                {
                                    "id": 2,
                                    "title": "긴급 핫픽스",
                                    "content": "버그 수정을 위한 긴급 패치입니다.",
                                    "type": "HOTFIX",
                                    "createdAt": "2023-12-01T12:00:00",
                                    "updatedAt": "2023-12-01T12:00:00"
                                }
                            ]
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "EVENT 타입 예시",
                        value = """
                        {
                            "success": true,
                            "message": "success",
                            "data": [
                                {
                                    "id": 3,
                                    "title": "겨울 이벤트",
                                    "content": "12월 특별 이벤트를 진행합니다.",
                                    "type": "EVENT",
                                    "createdAt": "2023-12-01T14:00:00",
                                    "updatedAt": "2023-12-01T14:00:00"
                                }
                            ]
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "UPDATE 타입 예시",
                        value = """
                        {
                            "success": true,
                            "message": "success",
                            "data": [
                                {
                                    "id": 4,
                                    "title": "버전 업데이트",
                                    "content": "v2.1.0 업데이트가 배포되었습니다.",
                                    "type": "UPDATE",
                                    "createdAt": "2023-12-01T16:00:00",
                                    "updatedAt": "2023-12-01T16:00:00"
                                }
                            ]
                        }
                        """
                    )
                }
            )
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 공지사항 타입")
    })
    public ResponseEntity<APIResponse<List<NoticeResponse>, Void>> getNoticesByType(
            @Parameter(
                description = "공지사항 타입",
                example = "NOTICE",
                schema = @Schema(allowableValues = {"NOTICE", "HOTFIX", "EVENT", "UPDATE"})
            ) @PathVariable NoticeType type) {
        List<Notice> notices = noticeService.getNoticesByType(type);
        List<NoticeResponse> response = notices.stream()
                .map(NoticeResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(APIResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "공지사항 삭제",
        description = "특정 공지사항을 삭제합니다. 모든 타입(NOTICE, HOTFIX, EVENT, UPDATE)의 공지사항을 삭제할 수 있습니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "공지사항 삭제 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = APIResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "success": true,
                        "message": "공지사항이 삭제되었습니다.",
                        "data": null
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "삭제할 공지사항을 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                        "success": false,
                        "error": "NOT_FOUND",
                        "message": "삭제할 공지사항을 찾을 수 없습니다."
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<APIResponse<Void, Void>> deleteNotice(
            @Parameter(description = "삭제할 공지사항 ID", example = "1") @PathVariable Long id) {
        try {
            noticeService.deleteNotice(id);
            return ResponseEntity.ok(APIResponse.success("공지사항이 삭제되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(APIResponse.fail("NOT_FOUND", "삭제할 공지사항을 찾을 수 없습니다."));
        }
    }
}