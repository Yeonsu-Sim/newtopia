package io.ssafy.p.i13c203.gameserver.domain.hotnews.dto;

import io.ssafy.p.i13c203.gameserver.domain.hotnews.entity.HotNews;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record HotNewsDto(
  String sourceUrl,
  String title,
  LocalDateTime publishedAt
) {

  public static HotNewsDto from(HotNews hotNews) {
    var dateOnly = hotNews.getPublishedAt() != null ? hotNews.getPublishedAt().toLocalDate().atStartOfDay() : null;

    return HotNewsDto.builder()
      .sourceUrl(hotNews.getSourceUrl())
      .title(hotNews.getTitle())
      .publishedAt(dateOnly)
      .build();
  }
}
