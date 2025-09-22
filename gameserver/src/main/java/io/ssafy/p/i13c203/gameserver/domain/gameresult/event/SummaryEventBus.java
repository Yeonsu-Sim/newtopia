package io.ssafy.p.i13c203.gameserver.domain.gameresult.event;

import io.ssafy.p.i13c203.gameserver.domain.gameresult.model.SummaryStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class SummaryEventBus {
    private final ConcurrentHashMap<Long, Sinks.Many<SummaryStatusChanged>> channels = new ConcurrentHashMap<>();

    public Sinks.Many<SummaryStatusChanged> channel(Long gameId) {
        return channels.computeIfAbsent(gameId, k ->
                Sinks.many().multicast().onBackpressureBuffer());
    }

    // 바로 Flux로 받고 싶을 때
    public Flux<SummaryStatusChanged> flux(Long gameId) {
        return channel(gameId).asFlux(); // Many → Flux
    }

    public void publish(Long gameId, SummaryStatus status) {
        channel(gameId).tryEmitNext(new SummaryStatusChanged(gameId, status));
    }

    public record SummaryStatusChanged(Long gameId, SummaryStatus status) {}
}
