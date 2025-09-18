package io.ssafy.p.i13c203.gameserver.domain.hotnews.service;

import io.ssafy.p.i13c203.gameserver.domain.hotnews.dto.HotNewsDto;
import io.ssafy.p.i13c203.gameserver.domain.hotnews.repository.HotNewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotNewsService {
  private final HotNewsRepository hotNewsLongJpaRepository;

  @Transactional(readOnly = true)
  public List<HotNewsDto> findTopNByPublishedAtDesc(Integer n) {
    log.trace("fetching top {} hotNews", n);
    return hotNewsLongJpaRepository.findByOrderByPublishedAtDesc(PageRequest.of(0,n))
      .stream().map(HotNewsDto::from).toList();
  }
}
