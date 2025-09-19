package io.ssafy.p.i13c203.gameserver.domain.gameresult.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.doc.ReportContextDoc;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "game_result",
        indexes = { @Index(name = "ux_game_result_game", columnList = "game_id", unique = true) })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GameResult {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_id", nullable = false, unique = true)
    private Long gameId;

    @Type(JsonType.class)
    @Column(name = "context", columnDefinition = "jsonb", nullable = false)
    private ReportContextDoc context;   // countryName, finalTurnNumber, generatedAt, final stats

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
