package io.ssafy.p.i13c203.news_crawler.service.scheduler;

import io.ssafy.p.i13c203.news_crawler.dto.ParsedNewsContent;
import io.ssafy.p.i13c203.news_crawler.entity.HotNews;
import io.ssafy.p.i13c203.news_crawler.repository.HotNewsRepository;
import io.ssafy.p.i13c203.news_crawler.service.crawler.YnaCrawlingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class YnaHotNewsScheduler {

    private final YnaCrawlingService ynaCrawlingService;
    private final HotNewsRepository hotNewsRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Scheduled(fixedRate = 3600000) // 1시간마다 실행 (3600000ms = 1시간)
    public void crawlAndSaveHotNews() {
        log.info("Starting hot news crawling at {}", LocalDateTime.now().format(DATE_FORMATTER));

        ynaCrawlingService.fetchAndParsing(List.of())
            .flatMap(this::saveIfNotExists)
            .doOnNext(saved -> {
                if (saved) {
                    log.info("New hot news saved");
                } else {
                    log.debug("Hot news already exists, skipped");
                }
            })
            .doOnError(error -> log.error("Error during hot news crawling: {}", error.getMessage()))
            .onErrorContinue((error, item) -> log.error("Failed to process item: {}", item, error))
            .subscribe();
    }

    private Mono<Boolean> saveIfNotExists(ParsedNewsContent parsedNews) {
        return hotNewsRepository.existsByTitle(parsedNews.title())
            .flatMap(exists -> {
                if (exists) {
                    return Mono.just(false);
                } else {
                    return saveHotNews(parsedNews).map(saved -> true);
                }
            });
    }

    private Mono<HotNews> saveHotNews(ParsedNewsContent parsedNews) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime publishedAt = parsePublishedAt(parsedNews.publishedAt());

        HotNews hotNews = HotNews.builder()
            .sourceUrl(parsedNews.sourceUrl())
            .title(parsedNews.title())
            .content(parsedNews.content())
            .publishedAt(publishedAt)
            .createdAt(now)
            .updatedAt(now)
            .build();

        return hotNewsRepository.save(hotNews);
    }

    private LocalDateTime parsePublishedAt(String publishedAtString) {
        try {
            return LocalDateTime.parse(publishedAtString, DATE_FORMATTER);
        } catch (Exception e) {
            log.warn("Failed to parse published date: {}, using current time", publishedAtString);
            return LocalDateTime.now();
        }
    }
}
