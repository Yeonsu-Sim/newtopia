package io.ssafy.p.i13c203.gameserver.domain.ranking.entity;

import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
  name = "rankings",
  indexes = {
    @Index(name = "ix_rankings_score_desc_id_asc", columnList = "score DESC, rankingId ASC")
  }
)
public class Ranking {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long rankingId;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id", nullable = false, unique = true)
  private Game game;

  @Column(nullable = false)
  private Long score;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @CreationTimestamp
  private LocalDateTime createdAt;
}


