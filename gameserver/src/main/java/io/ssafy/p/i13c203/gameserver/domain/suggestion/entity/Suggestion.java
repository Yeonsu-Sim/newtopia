package io.ssafy.p.i13c203.gameserver.domain.suggestion.entity;


import io.ssafy.p.i13c203.gameserver.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "suggestions")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Suggestion {

    @Id @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // many to one suggestion 입장에서
    // Suggestion N : 1 Member
    @ManyToOne
    private Member member;

    @Column(nullable = false)
    private String title;

    // Enumerated ->
    @Enumerated(EnumType.STRING)
    @Column(name = "suggestion_category")
    private SuggestionCategory category;

    @Column(nullable = false)
    private String text;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
