package me.yeon.freship.orders.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisOrderLockRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<Boolean> checkAndReleaseLockScript;

    private static final String LOCK_PREFIX = "LOCK:";
    private static final long TIME_TO_LIVE = 300;

    public Boolean lock(String key, String requestUuid) {
        return redisTemplate
                .opsForValue()
                .setIfAbsent(createKey(key), requestUuid, Duration.ofMillis(TIME_TO_LIVE));
    }

    public Boolean unlock(String key, String uuid) {
        return redisTemplate.execute(
                checkAndReleaseLockScript,
                List.of(createKey(key)),
                uuid
        );
    }

    private String createKey(String key) {
        return LOCK_PREFIX + key;
    }
}
