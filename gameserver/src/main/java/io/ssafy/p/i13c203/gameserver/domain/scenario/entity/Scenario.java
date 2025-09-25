package io.ssafy.p.i13c203.gameserver.domain.scenario.entity;


import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.ssafy.p.i13c203.gameserver.domain.game.model.CardType;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.ChoiceDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.RelatedArticleDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.SpawnConditionsDoc;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "scenario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // NPC는 FK로 관리
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "npc_id", nullable = false)
    private Npc npc;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private SpawnConditionsDoc spawn;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, ChoiceDoc> choices;


    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private RelatedArticleDoc relatedArticle;  // 원본 기사 정보


    // ----- MVP 3차 추가 -----
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CardType type;

    private String articleId;  // 원본 기사 ID (쿼리용)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_scenario_id")
    private Scenario originScenario;  // 원본 시나리오 (스노우볼 시나리오 용)

    // ----- -----

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}