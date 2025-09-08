package io.ssafy.p.i13c203.news_crawler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ParsedNewsContent(
    @JsonProperty("source_url") String sourceUrl,
    String title,
    String content,
    @JsonProperty("published_at") String publishedAt) {
}