package io.ssafy.p.i13c203.gameserver.domain.gameresult.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.doc.ReportSummaryDoc;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "game_result_summary",
        indexes = { @Index(name = "ux_game_result_summary_result", columnList = "game_result_id", unique = true) })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GameResultSummary {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_result_id", nullable = false, unique = true)
    private Long gameResultId;   // FK(GameResult.id)

    @Type(JsonType.class)
    @Column(name = "summary", columnDefinition = "jsonb", nullable = false)
    private ReportSummaryDoc summary;   // status/promptHash/sections/subscribeUrl

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
