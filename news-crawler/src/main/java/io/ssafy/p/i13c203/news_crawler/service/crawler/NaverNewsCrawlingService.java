package io.ssafy.p.i13c203.news_crawler.service.crawler;

import org.jsoup.Jsoup;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.ssafy.p.i13c203.news_crawler.dto.ParsedNewsContent;
import io.ssafy.p.i13c203.news_crawler.service.parser.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverNewsCrawlingService implements CrawlingService {

  private final Converter naverNewsConverter;
  private final WebClient naverNewsClient;

  private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

  @Override
  public Mono<ParsedNewsContent> fetchAndParsing(String url) {
    return fetch(url)
      .doOnNext(res -> log.trace("response length={} for {}", res.length(), url))
      .publishOn(Schedulers.parallel())
      .map(this::parsing)
      .onErrorResume(e -> Mono.empty());
  }

  @Override
  public Flux<ParsedNewsContent> fetchAndParsing(List<String> url) {
    log.info("Starting crawling for {} URLs", url.size());
    return Flux.fromIterable(url)
      .buffer(100) // 100개씩 배치로 묶기
      .concatMap(batch ->
        Flux.fromIterable(batch)
          .doOnNext(u -> log.debug("Processing URL: {}", u))
          .flatMap(this::fetch, AVAILABLE_PROCESSORS * 4)
          .parallel(AVAILABLE_PROCESSORS)
          .runOn(Schedulers.parallel())
          .flatMap(html -> {
            try {
              ParsedNewsContent result = parsing(html);
              log.debug("Successfully parsed: {}", result.title());
              return Mono.just(result);
            } catch (Exception e) {
              log.error("Failed to parse HTML content: {}", e.getMessage(), e);
              return Mono.empty();
            }
          })
          .sequential()
          .doOnComplete(() -> log.info("Completed batch of {} URLs", batch.size()))
          .delaySubscription(Duration.ofMillis(500))
      );

  }

  private Mono<String> fetch(String url) {
    return naverNewsClient.get().uri(url)
      .header("User-Agent", "Mozilla/5.0")
      .header("Accept-Language", "ko-KR,ko;q=0.9")
      .retrieve()
      .onStatus(HttpStatusCode::is4xxClientError, resp -> {
        int statusCode = resp.statusCode().value();
        log.warn("4xx Client Error: {} for URL: {}", statusCode, url);
        return resp.releaseBody().then(Mono.empty());
      })
      .onStatus(HttpStatusCode::is5xxServerError, resp -> {
        int statusCode = resp.statusCode().value();
        log.warn("5xx Server Error: {} for URL: {}", statusCode, url);
        return resp.releaseBody().then(Mono.empty());
      })
      .bodyToMono(String.class)
      .retryWhen(Retry.backoff(2, Duration.ofMillis(200))
        .filter(e -> e instanceof WebClientResponseException.TooManyRequests || e instanceof IOException)
        .doBeforeRetry(signal -> log.warn("Retrying request for URL: {} (attempt: {})", url, signal.totalRetries() + 1)))
      .onErrorResume(e -> {
        log.error("Failed to fetch URL: {} - {}", url, e.getMessage());
        return Mono.empty();
      });
  }

  private ParsedNewsContent parsing(String html) {
    return naverNewsConverter.convert(Jsoup.parse(html));
  }
}
