package io.ssafy.p.i13c203.gameserver.domain.image.entity;


import io.ssafy.p.i13c203.gameserver.domain.suggestion.entity.Suggestion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="suggestion_images")
@Getter
@Builder
@NoArgsConstructor@AllArgsConstructor
public class SuggestionImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    private Suggestion suggestion;

    @OneToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name = "image_id", unique = true)
    private Image image;

    @Column(name="sort_order", nullable=false)
    private int sortOrder; // 0..N

}
