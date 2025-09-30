package io.ssafy.p.i13c203.gameserver.domain.game.service;

import io.ssafy.p.i13c203.gameserver.domain.game.repository.EventIntervalRepository;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventIntervalService {

    private final EventIntervalRepository repository;

    private static final int MIN_INITIAL_INTERVAL = 2;
    private static final int MAX_INITIAL_INTERVAL = 4;

    /** 초기화 */
    public void init(Long gameId, int currentTurn) {
        int initialInterval = randomInterval();
        int nextTurn = currentTurn + initialInterval;
        repository.save(gameId, initialInterval, nextTurn);
        log.debug("Initialized gameId={} event interval={} event turn={}", gameId, initialInterval, nextTurn);
    }

    /** 이벤트 발생 여부 */
    public boolean shouldTrigger(Long gameId, int currentTurn) {
//        // TODO: HARD CODING for Presentation
//        if(currentTurn == 3) {
//            return true;
//        }

        String next = repository.findField(gameId, "nextEventTurn");
        log.debug("nextEventTurn: {} gameId: {}", next, gameId);
        if (next == null) {
            init(gameId, currentTurn);
            next = repository.findField(gameId, "nextEventTurn");
        }
        return currentTurn >= Integer.parseInt(next);
    }

    /** 이벤트 발생 후 업데이트 */
    public void updateAfterTrigger(Long gameId, int currentTurn) {
        // 매번 랜덤 인터벌 생성
        int interval = randomInterval();

        int nextTurn = currentTurn + interval;
        repository.save(gameId, interval, nextTurn);

        log.debug("Updated interval={} nextTurn={} for gameId={}", interval, nextTurn, gameId);
    }

    /** 게임 종료/비활성화 시 */
    public void clear(Long gameId) {
        repository.delete(gameId);
    }

    /** 랜덤 초기값 생성 */
    private int randomInterval() {
        return ThreadLocalRandom.current().nextInt(MIN_INITIAL_INTERVAL, MAX_INITIAL_INTERVAL + 1);
    }
}
