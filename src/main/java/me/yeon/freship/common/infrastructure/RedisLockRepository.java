package me.yeon.freship.common.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisLockRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LOCK_PREFIX = "LOCK:";
    private static final long TIME_TO_LIVE = 8000;

    public Boolean lock(String key) {
        return redisTemplate
                .opsForValue()
                .setIfAbsent(LOCK_PREFIX + key, "lock", Duration.ofMillis(TIME_TO_LIVE));
    }

    public Boolean unlock(String key) {
        return redisTemplate.delete(LOCK_PREFIX + key);
    }
}
