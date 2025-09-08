package io.ssafy.p.i13c203.news_crawler.service.parser;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import io.ssafy.p.i13c203.news_crawler.dto.ParsedNewsContent;

@Slf4j
@Component
public class NaverConverter implements Converter {

  @Override
  public ParsedNewsContent convert(Document document) {
    var title = document.select("#title_area").first();
    var article = document.select("#dic_area").first();
    var publishAt = document.select(".media_end_head_info_datestamp_time._ARTICLE_DATE_TIME").first();
    var metaOgUrl = document.select("meta[property=og:url]").first();

    String url = metaOgUrl != null ? metaOgUrl.attr("content") : "";
    String titleText = title != null ? title.text() : "";
    String articleText = article != null ? article.text() : "";
    String publishText = publishAt != null ? publishAt.text() : "";

    if (url.isEmpty() || titleText.isEmpty() || articleText.isEmpty()) {
      log.warn("Incomplete parsing - URL: {}, Title empty: {}, Article empty: {}, Publish empty: {}",
        url.isEmpty() ? "EMPTY" : url,
        titleText.isEmpty(),
        articleText.isEmpty(),
        publishText.isEmpty());
    }

    return new ParsedNewsContent(url, titleText, articleText, publishText);
  }

}
