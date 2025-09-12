package io.ssafy.p.i13c203.gameserver.domain.ending.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(name="ending_s3_key")
    private String endingS3Key;

    @Column(nullable=false)
    private Integer count = 0;         // 수집횟수

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
