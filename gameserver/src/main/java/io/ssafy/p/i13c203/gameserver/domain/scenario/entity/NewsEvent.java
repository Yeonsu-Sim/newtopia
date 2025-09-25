package io.ssafy.p.i13c203.gameserver.domain.scenario.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.NewsCategoryDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.SentimentDoc;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "news_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NewsEvent {

    @Id
    @Column(length = 20)
    private String articleId;

    @Column(name = "source_url", nullable = false)
    private String sourceUrl;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "published_at", nullable = false)
    private LocalDateTime publishedAt;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", name = "categories", nullable = false)
    private NewsCategoryDoc newsCategoryDoc;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", name = "sentiment", nullable = false)
    private SentimentDoc sentimentDoc;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    @Builder
    public NewsEvent(String articleId, String sourceUrl, String title, String content,
            LocalDateTime publishedAt, NewsCategoryDoc newsCategoryDoc, SentimentDoc sentimentDoc,
            LocalDateTime processedAt) {
        this.articleId = articleId;
        this.sourceUrl = sourceUrl;
        this.title = title;
        this.content = content;
        this.publishedAt = publishedAt;
        this.newsCategoryDoc = newsCategoryDoc;
        this.sentimentDoc = sentimentDoc;
        this.processedAt = processedAt;
    }
}
