package io.ssafy.p.i13c203.gameserver.domain.game.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.ssafy.p.i13c203.gameserver.domain.ending.doc.EndingDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.CardDoc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.doc.ChoiceDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.model.ChoiceWeights;
import io.ssafy.p.i13c203.gameserver.domain.game.model.CountryStats;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "game")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = true)
    private String endingCode;

    @Column(nullable = false, length = 64)
    private String countryName;

    @Embedded
    private CountryStats countryStats; // 0 .. 100

    @Embedded
    private ChoiceWeights choiceWeights; // 0.0 .. 1.0

    @Column(name = "turn_number", nullable = false)
    private int turn;

    @Version
    private Long version;

    private String choosedCode;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    private CardDoc currentCard;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, ChoiceDoc> currentChoices;

    @Column(nullable = false)
    private boolean active;

    @Column
    private Instant endedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void markEnded(EndingDoc ending) {
        this.endingCode = ending.code();
        this.endedAt = Instant.now();
        this.active = false;
    }
}
