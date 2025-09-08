package io.ssafy.p.i13c203.news_crawler.service.scheduler;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import io.ssafy.p.i13c203.news_crawler.dto.NaverNewsApiContent;
import io.ssafy.p.i13c203.news_crawler.dto.NaverNewsApiElement;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class NaverNewsScheduler {
  private final WebClient naverNewsApiClient;

  public Mono<?> fetchNaverNews() {
    System.out.println(URLEncoder.encode("광역시", StandardCharsets.UTF_8));
    return naverNewsApiClient
      .get()
      .uri(uriBuilder -> uriBuilder
        .queryParam("query", "전쟁")
        .queryParam("display", 100)
        .queryParam("sort", "sim")
        .queryParam("start", 1)
        .build())
      .retrieve()
      .bodyToMono(Map.class)
      .publishOn(Schedulers.boundedElastic())
      .doOnNext(response -> {
        System.out.println("Naver News API Response: " + response);
      })
      .doOnError(error -> {
        System.err.println("Error fetching Naver News: " + error.getMessage());
      });
  }

  public Flux<NaverNewsApiElement> fetchNaverNews(String keyword) {
    return naverNewsApiClient.get()
      .uri(uriBuilder -> uriBuilder
        .queryParam("query", "서울")
        .queryParam("display", 100)
        .queryParam("sort", "date")
        .queryParam("start", 100)
        .build())
      .retrieve().bodyToMono(NaverNewsApiContent.class)
      .publishOn(Schedulers.boundedElastic())
      .flatMapMany(res -> Flux.fromIterable(res.items()));
  }

}
