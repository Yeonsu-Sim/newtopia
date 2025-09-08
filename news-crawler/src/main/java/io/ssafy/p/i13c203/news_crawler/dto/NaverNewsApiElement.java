package io.ssafy.p.i13c203.news_crawler.dto;

public record NaverNewsApiElement(
        String title,
        String originallink,
        String link,
        String description,
        String pubDate
) {}
