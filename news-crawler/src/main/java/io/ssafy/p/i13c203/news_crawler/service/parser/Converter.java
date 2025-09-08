package io.ssafy.p.i13c203.news_crawler.service.parser;

import org.jsoup.nodes.Document;

import io.ssafy.p.i13c203.news_crawler.dto.ParsedNewsContent;

public interface Converter {
  ParsedNewsContent convert(Document document);
}