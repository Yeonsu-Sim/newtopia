package io.ssafy.p.i13c203.news_crawler.service.dataLoader;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ssafy.p.i13c203.news_crawler.dto.ParsedNewsContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlingDataLoaderService {

  private final ObjectMapper objectMapper;
  private final ResourcePatternResolver resourcePatternResolver;

  public Flux<Resource> findCrawlingResultFiles() {
    return Mono.fromCallable(() -> {
      try {
        Resource[] resources = resourcePatternResolver.getResources("classpath:dummy/crawling-results-*.json");
        log.info("Found {} crawling result files in classpath:dummy/", resources.length);
        return Arrays.asList(resources);
      } catch (IOException e) {
        log.error("Failed to list crawling result files from classpath", e);
        return List.<Resource>of();
      }
    }).flatMapMany(Flux::fromIterable);
  }

  public Flux<ParsedNewsContent> loadCrawlingResults(List<Resource> resources) {
    return Flux.fromIterable(resources)
      .concatMap(this::loadSingleFileAsStream)
      .doOnNext(content -> log.debug("Loaded news content: {}", content.title()))
      .doOnError(e -> log.error("Error loading crawling results", e));
  }

  public Flux<ParsedNewsContent> loadSingleFileAsStream(Resource resource) {
    return loadSingleFile(resource);
  }

  private Flux<ParsedNewsContent> loadSingleFile(Resource resource) {
    return Mono.fromCallable(() -> {
      try (InputStream inputStream = resource.getInputStream()) {
        CrawlingResult crawlingResult = objectMapper.readValue(inputStream, CrawlingResult.class);
        String filename = resource.getFilename() != null ? resource.getFilename() : "unknown";
        log.info("Loaded {} results from classpath resource: {}", crawlingResult.results().size(), filename);

        return crawlingResult.results();
      } catch (IOException e) {
        String filename = resource.getFilename() != null ? resource.getFilename() : "unknown";
        log.error("Failed to read classpath resource: {}", filename, e);
        return List.<ParsedNewsContent>of();
      }
    }).flatMapMany(Flux::fromIterable);
  }

  private record CrawlingResult(
    int totalRequested,
    int successfullyParsed,
    long elapsedMs,
    String timestamp,
    List<ParsedNewsContent> results
  ) {
  }
}