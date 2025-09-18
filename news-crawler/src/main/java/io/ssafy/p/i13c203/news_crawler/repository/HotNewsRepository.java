package io.ssafy.p.i13c203.news_crawler.repository;

import io.ssafy.p.i13c203.news_crawler.entity.HotNews;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface HotNewsRepository extends ReactiveCrudRepository<HotNews, Long> {

    @Query("SELECT EXISTS(SELECT 1 FROM hot_news WHERE title = :title)")
    Mono<Boolean> existsByTitle(String title);
}