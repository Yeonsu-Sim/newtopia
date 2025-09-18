package io.ssafy.p.i13c203.news_crawler.service.crawler;

import java.util.List;

import io.ssafy.p.i13c203.news_crawler.service.parser.YnaHistoryParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.ssafy.p.i13c203.news_crawler.dto.ParsedNewsContent;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class YnaCrawlingService implements CrawlingService {
  private final String YNA_NEWS_URL = "https://www.yna.co.kr/theme/hotnews-history";
  private final WebClient ynaNewsClient;

  private final YnaHistoryParser ynaHistoryParser;

  public Mono<ParsedNewsContent> fetchAndParsing(String url) {
    throw new UnsupportedOperationException("Unimplemented method 'Mono<?> fetchAndParsing'");
  }

  @Override
  public Flux<ParsedNewsContent> fetchAndParsing(List<String> url) {
    return ynaNewsClient.get().uri(YNA_NEWS_URL).retrieve()
      .bodyToMono(String.class)
      .publishOn(Schedulers.parallel())
      .map(html -> ynaHistoryParser.convert(Jsoup.parse(html)))
      .flatMapMany(Flux::fromIterable);
  }
  
}
