package io.ssafy.p.i13c203.gameserver.domain.ending.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(name="ending_s3_key")
    private String endingS3Key;

    @Column(nullable=false)
    @Builder.Default
    private Integer count = 0;         // 수집횟수

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
