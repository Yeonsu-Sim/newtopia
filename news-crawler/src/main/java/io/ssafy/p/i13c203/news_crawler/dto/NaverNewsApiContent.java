package io.ssafy.p.i13c203.news_crawler.dto;

import java.util.List;

public record NaverNewsApiContent(
        String lastBuildDate,
        Integer total,
        Integer start,
        Integer display,
        List<NaverNewsApiElement> items
){}