package io.ssafy.p.i13c203.gameserver.domain.game.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.HistoryEntryDoc;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "game_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @Column(name = "turn_number", nullable = false)
    @Builder.Default
    private int turn = 0;

    @Type(JsonType.class)
    @Column(name = "entry", columnDefinition = "jsonb", nullable = false)
    private HistoryEntryDoc entry;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}