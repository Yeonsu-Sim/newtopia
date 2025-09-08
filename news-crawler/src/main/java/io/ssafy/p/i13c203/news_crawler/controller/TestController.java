package io.ssafy.p.i13c203.news_crawler.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import io.ssafy.p.i13c203.news_crawler.dto.ParsedNewsContent;
import io.ssafy.p.i13c203.news_crawler.service.crawler.NaverNewsCrawlingService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ssafy.p.i13c203.news_crawler.service.dataLoader.CrawlingDataLoaderService;
import io.ssafy.p.i13c203.news_crawler.service.producer.NewsContentProducerService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {
    private final NaverNewsCrawlingService naverNewsCrawlingService;
    private final CrawlingDataLoaderService crawlingDataLoaderService;
    private final NewsContentProducerService newsContentProducerService;

    @GetMapping("/test")
    public Mono<ParsedNewsContent> testNaverNew(@RequestParam String url) {
        return naverNewsCrawlingService.fetchAndParsing(url);
    }

    @GetMapping("/test/flux")
    public Mono<String> testNaverNewsFlux(
            @RequestParam(defaultValue = "366") int office,
            @RequestParam(defaultValue = "1105068") int startId,
            @RequestParam(defaultValue = "1000") int count
    ) {
        List<String> urls = IntStream.range(0, count)
                .mapToObj(i -> "https://n.news.naver.com/mnews/article/%d/%010d".formatted(office, startId + i))
                .toList();

        return Mono.defer(() -> {
            Instant start = Instant.now();
            return naverNewsCrawlingService.fetchAndParsing(urls)
                    .collectList()
                    .doOnNext(results -> {
                        try {
                            saveToJsonFile(results, count, start);
                        } catch (Exception e) {
                            log.error("Failed to save JSON file", e);
                        }
                    })
                    .map(results -> "count=%d, success=%d, elapsedMs=%d, saved=crawling-results-%s.json"
                            .formatted(count, results.size(), 
                                    Duration.between(start, Instant.now()).toMillis(),
                                    DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now())));
        });
    }

    private void saveToJsonFile(List<ParsedNewsContent> results, int totalCount, Instant startTime) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        
        CrawlingResult crawlingResult = new CrawlingResult(
            totalCount,
            results.size(),
            Duration.between(startTime, Instant.now()).toMillis(),
            LocalDateTime.now().toString(),
            results
        );

        String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now());
        String filename = String.format("crawling-results-%s.json", timestamp);
        
        // resources/dummy 디렉토리에 저장
        Path resourcesPath = Paths.get("src/main/resources/dummy");
        if (!Files.exists(resourcesPath)) {
            Files.createDirectories(resourcesPath);
        }
        
        Path filePath = resourcesPath.resolve(filename);
        String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(crawlingResult);
        Files.write(filePath, jsonString.getBytes());
        
        log.info("Saved crawling results to: {}", filePath.toAbsolutePath());
    }

    @GetMapping("/load-crawling-data")
    public Mono<String> loadCrawlingDataToKafka() {
        Instant start = Instant.now();
        
        return crawlingDataLoaderService.findCrawlingResultFiles()
                .collectList()
                .doOnNext(resources -> log.info("Found {} crawling result files from classpath:dummy/", resources.size()))
                .flatMap(resources -> {
                    if (resources.isEmpty()) {
                        return Mono.just("No crawling result files found in classpath:dummy/");
                    }
                    
                    return crawlingDataLoaderService.loadCrawlingResults(resources)
                            .buffer(500)
                            .concatMap(batch -> {
                                log.info("Processing batch of {} news contents", batch.size());
                                return newsContentProducerService.produceNewsContent(batch);
                            })
                            .reduce(0L, Long::sum)
                            .map(totalSent -> String.format(
                                    "Successfully sent %d news contents to Kafka in %d ms",
                                    totalSent,
                                    Duration.between(start, Instant.now()).toMillis()
                            ));
                })
                .doOnError(e -> log.error("Error loading crawling data to Kafka", e))
                .onErrorReturn("Failed to load crawling data to Kafka");
    }

    private record CrawlingResult(
        int totalRequested,
        int successfullyParsed, 
        long elapsedMs,
        String timestamp,
        List<ParsedNewsContent> results
    ) {}
}