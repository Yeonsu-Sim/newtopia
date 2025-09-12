package io.ssafy.p.i13c203.gameserver.domain.game.idem.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IdempotentOperation {
    String headerName() default "Idempotency-Key";
    String[] hashArgs() default {};
    long ttlSeconds() default 600L;
    long lockSeconds() default 10L; // 분산락 TTL
    long waitMillis() default 800L; // PENDING 시 대기 최대 시간
    long spinIntervalMillis() default 40L; // 폴링 간격
}
