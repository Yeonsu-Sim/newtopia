package io.ssafy.p.i13c203.gameserver.domain.hotnews.controller;

import io.ssafy.p.i13c203.gameserver.domain.hotnews.dto.HotNewsDto;
import io.ssafy.p.i13c203.gameserver.domain.hotnews.service.HotNewsService;
import io.ssafy.p.i13c203.gameserver.global.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class HotNewsControllerImpl implements HotNewsController {

  private final HotNewsService hotNewsService;

  @Override
  @GetMapping("/hotnews")
  public ResponseEntity<APIResponse<List<HotNewsDto>, Void>> getRecentNHotNews(@RequestParam(defaultValue = "20") Integer limit) {
    log.trace("getRecentNHotNews({})", limit);
    var body = hotNewsService.findTopNByPublishedAtDesc(limit);
    log.trace("getRecentNHotNews({})", body);
    return ResponseEntity.ok(APIResponse.success("최근 %d개의 핫 뉴스 요청 중 %d 개가 발견됨".formatted(limit,body.size()),body));
  }
}
