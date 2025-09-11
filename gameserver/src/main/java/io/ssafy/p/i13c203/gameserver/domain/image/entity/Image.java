package io.ssafy.p.i13c203.gameserver.domain.image.entity;

import io.ssafy.p.i13c203.gameserver.domain.member.entity.Member;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.entity.Suggestion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "images")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 외부 노출/ 조회용 ( API에서 files/{publicId} 형태)
    @UuidGenerator
    @Column(name = "public_id", columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID publicId;

    @Column(nullable = false)
    private String storageKey;


    @Setter
    @ManyToOne
    private Suggestion suggestion;

    @Enumerated(EnumType.STRING)
    private ImageStatus status;

    @Column(nullable = false)
    private Long sizeBytes;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private String originalName;

    @ManyToOne
    private Member member;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
