package me.yeon.freship.common.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class RedisClear {

    private final RedisTemplate<String,Object> redisTemplate;

    @Scheduled(cron = "0 0 0 * * ?")  // 자정에 캐시 리셋
    public void clearCache() {

        Set<String> cacheKeys = redisTemplate.keys("popular:search*");
        if (cacheKeys != null && !cacheKeys.isEmpty()) {
            redisTemplate.delete(cacheKeys);
        }

        // 유저 검색 기록 삭제
        Set<String> keysForDelete = redisTemplate.keys("user:*");
        if (keysForDelete != null && !keysForDelete.isEmpty()) {
            redisTemplate.delete(keysForDelete);
        }

        // 검색 조회수 삭제
        if (Boolean.TRUE.equals(redisTemplate.hasKey("rank"))) {
            redisTemplate.opsForZSet().removeRange("rank", 0, -1);
        }
    }
}
