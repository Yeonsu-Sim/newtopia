package io.ssafy.p.i13c203.gameserver.domain.game.idem.redis;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("idem")
public class IdemRecord {
    public enum Status { PENDING, FINAL }

    @Id
    private String key; // Idempotency-Key 헤더 값
    private String requestHash; // memberId+method+args 등
    private Status status;
    private String responseJson; // 최종 응답 캐시(JSON 직렬화)
    private Instant createdAt;
    private Instant updatedAt;


    @TimeToLive
    private Long ttlSeconds; // 예: 600
}