package io.ssafy.p.i13c203.gameserver.domain.game.idem.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ssafy.p.i13c203.gameserver.auth.security.CustomUserDetails;
import io.ssafy.p.i13c203.gameserver.domain.game.idem.annotation.IdempotentOperation;
import io.ssafy.p.i13c203.gameserver.domain.game.idem.redis.IdemRecord;
import io.ssafy.p.i13c203.gameserver.domain.game.repository.IdemRecordRepository;
import io.ssafy.p.i13c203.gameserver.global.exception.BusinessException;
import io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {
    private final IdemRecordRepository repo;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;


    @Around("@annotation(io.ssafy.p.i13c203.gameserver.domain.game.idem.annotation.IdempotentOperation)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        Method method = sig.getMethod();
        IdempotentOperation ann = method.getAnnotation(IdempotentOperation.class);


        String headerName = ann.headerName();
        String idemKey = getHeader(headerName);
        if (idemKey == null || idemKey.isBlank()) {
            return pjp.proceed(); // 키가 없으면 멱등성 비적용
        }


        String reqHash = buildRequestHash(sig, pjp.getArgs(), ann.hashArgs());
        String lockKey = "idem:lock:" + idemKey;


        Optional<IdemRecord> existing = repo.findById(idemKey);
        if (existing.isPresent()) {
            IdemRecord rec = existing.get();
            if (!Objects.equals(rec.getRequestHash(), reqHash)) {
                throw new BusinessException(ErrorCode.IDEMPOTENCY_KEY_CONFLICT);
            }
            if (rec.getStatus() == IdemRecord.Status.FINAL && rec.getResponseJson() != null) {
                return objectMapper.readValue(rec.getResponseJson(), sig.getReturnType());
            }
            // PENDING 상태: 분산락 강화 모드
            boolean got = tryLock(lockKey, ann.lockSeconds());
            if (!got) {
                // 다른 노드/스레드가 처리 중 → 짧게 폴링 후 결과 있으면 반환
                long deadline = System.currentTimeMillis() + ann.waitMillis();
                while (System.currentTimeMillis() < deadline) {
                    Thread.sleep(ann.spinIntervalMillis());
                    Optional<IdemRecord> r2 = repo.findById(idemKey);
                    if (r2.isPresent() && r2.get().getStatus() == IdemRecord.Status.FINAL && r2.get().getResponseJson() != null) {
                        return objectMapper.readValue(r2.get().getResponseJson(), sig.getReturnType());
                    }
                }
                throw new BusinessException(ErrorCode.IDEMPOTENCY_IN_PROGRESS);
            }
        // 락을 획득했으면 계속 진행하여 최종화를 시도한다
        } else {
            // 최초 요청: PENDING 기록 + 락 획득 시에만 진행
            repo.save(IdemRecord.builder()
                    .key(idemKey)
                    .requestHash(reqHash)
                    .status(IdemRecord.Status.PENDING)
                    .createdAt(java.time.Instant.now())
                    .updatedAt(java.time.Instant.now())
                    .ttlSeconds(ann.ttlSeconds())
                    .build());
            if (!tryLock(lockKey, ann.lockSeconds())) {
                // 이론상 거의 없지만 경합 시 처리 중으로 간주
                throw new BusinessException(ErrorCode.IDEMPOTENCY_IN_PROGRESS);
            }
        }


        Object result;
        try {
            result = pjp.proceed();
        } finally {
            // 최종화는 finally 후반부에서 수행
        }
        try {
            String json = objectMapper.writeValueAsString(result);
            repo.findById(idemKey).ifPresent(r -> {
                r.setStatus(IdemRecord.Status.FINAL);
                r.setResponseJson(json);
                r.setUpdatedAt(java.time.Instant.now());
                repo.save(r);
            });
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.REDIS_ERROR, e.getMessage());
        } finally {
            unlock(lockKey);
        }
        return result;
    }


    private boolean tryLock(String lockKey, long lockSeconds) {
        try {
            Boolean ok = redis.opsForValue().setIfAbsent(lockKey, "1", Duration.ofSeconds(lockSeconds));
            return Boolean.TRUE.equals(ok);
        } catch (Exception e) { throw new BusinessException(ErrorCode.REDIS_ERROR, e.getMessage()); }
    }
    private void unlock(String lockKey) {
        try { redis.delete(lockKey); } catch (Exception ignored) {}
    }


    private String getHeader(String headerName) {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) {
            HttpServletRequest req = sra.getRequest();
            return req.getHeader(headerName);
        }
        return null;
    }


    private String buildRequestHash(MethodSignature sig, Object[] args, String[] hashArgs) {
        Map<String, Object> argMap = new LinkedHashMap<>();
        String[] names = sig.getParameterNames();
        for (int i = 0; i < names.length; i++) argMap.put(names[i], args[i]);
        StringBuilder sb = new StringBuilder();
        sb.append("member:").append(Objects.toString(currentMemberId())).append('|');
        sb.append("method:").append(sig.getDeclaringTypeName()).append('#').append(sig.getName()).append('|');
        for (String n : hashArgs) sb.append(n).append('=').append(argMap.get(n)).append('|');
        return Integer.toHexString(sb.toString().hashCode());
    }

    private String currentMemberId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return "anonymous";
        }
        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetails cud) {
            Long id = cud.getMemberId();
            return (id == null) ? "anonymous" : String.valueOf(id);
        }

        // JWT에서 username만 실린 경우나 다른 Provider인 경우 대비
        return auth.getName(); // 필요 시 "anonymous" 처리 로직 추가
    }
}