package io.ssafy.p.i13c203.news_crawler.service.crawler;

import io.ssafy.p.i13c203.news_crawler.dto.ParsedNewsContent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

public interface CrawlingService {
  Mono<ParsedNewsContent> fetchAndParsing(String url);

  Flux<ParsedNewsContent> fetchAndParsing(List<String> url);

  default Flux<ParsedNewsContent> fetchAndParsing(String[] url){
    return fetchAndParsing(Arrays.asList(url));
  }
}
