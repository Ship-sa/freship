package me.yeon.freship.common.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
public class RedisUtils {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisUtils(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setData(String key, Long value, Long expiredTime){
        redisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.MINUTES);
    }

    public Long getData(String key){
        return (Long) redisTemplate.opsForValue().get(key);
    }

    public Long incrementData(String key){
        return redisTemplate.opsForValue().increment(key);
    }

    public void deleteData(String key){
        redisTemplate.delete(key);
    }

    public boolean existsKey(String key){
        return redisTemplate.hasKey(key);
    }
}
