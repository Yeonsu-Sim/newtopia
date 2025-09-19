package io.ssafy.p.i13c203.news_crawler.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("hot_news")
public class HotNews {
    @Id
    private Long id;

    @Column("source_url")
    private String sourceUrl;

    @Column("title")
    private String title;

    @Column("content")
    private String content;

    @Column("published_at")
    private LocalDateTime publishedAt;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
