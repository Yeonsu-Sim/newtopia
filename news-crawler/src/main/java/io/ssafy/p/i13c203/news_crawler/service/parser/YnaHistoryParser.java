package io.ssafy.p.i13c203.news_crawler.service.parser;

import io.ssafy.p.i13c203.news_crawler.dto.ParsedNewsContent;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class YnaHistoryParser {
  public List<ParsedNewsContent> convert(Document document) {
    var lis = document.select(".news-con");

    return lis.stream()
        .map(this::parseNewsItem)
        .collect(Collectors.toList());
  }

  private ParsedNewsContent parseNewsItem(Element newsElement) {
    String sourceUrl = newsElement.select(".tit-news").attr("href");
    String title = newsElement.select(".title01").text();
    String publishedAt = newsElement.select(".txt-time").text();

    return new ParsedNewsContent(sourceUrl, title, title, publishedAt);
  }
}
