package io.ssafy.p.i13c203.gameserver.domain.ending.entity;

import java.time.LocalDateTime;

import io.ssafy.p.i13c203.gameserver.domain.image.entity.Image;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="endings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ending {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=100, unique = true)
    private String code;

    @Column(nullable=false)
    private String title;

    @Column(nullable=false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable=false, columnDefinition = "TEXT")
    private String condition;          // 설명용 (ex. economy==100)

    @OneToOne(fetch = FetchType.EAGER)
    private Image image;

    @Column(nullable=false)
    @ColumnDefault("0")
    @Builder.Default
    private Integer count = 0;         // TODO: 수집횟수 관련 기능 추가

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
