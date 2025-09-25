package io.ssafy.p.i13c203.gameserver.domain.game.repository;

import java.util.Map;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EventIntervalRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public EventIntervalRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String key(Long gameId) {
        return "game:" + gameId + ":eventInterval";
    }

    /** 저장 */
    public void save(Long gameId, int interval, int nextTurn) {
        redisTemplate.opsForHash().put(key(gameId), "eventInterval", String.valueOf(interval));
        redisTemplate.opsForHash().put(key(gameId), "nextEventTurn", String.valueOf(nextTurn));
    }

    /** 조회 */
    public Map<Object, Object> find(Long gameId) {
        return redisTemplate.opsForHash().entries(key(gameId));
    }

    /** 특정 필드 조회 */
    public String findField(Long gameId, String field) {
        return (String) redisTemplate.opsForHash().get(key(gameId), field);
    }

    /** 삭제 */
    public void delete(Long gameId) {
        redisTemplate.delete(key(gameId));
    }
}