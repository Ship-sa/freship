package me.yeon.freship.common.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor
public class RedisUtils {
    private final RedisTemplate<String, Object> redisTemplate;

    public void setScore(String key, Long id){
        redisTemplate.opsForZSet().add(key, "product:" + id, 1.0);
    }

    public Double incrementScore(String key, Long productId){
        return redisTemplate.opsForZSet().incrementScore(key, "product:" + productId, 1.0);
    }

    public boolean notExistsKey(String key, Long id){
        Double currentScore = redisTemplate.opsForZSet().score(key, "product:" + id);
        return currentScore == null;
    }

    public List<Long> findTop10ProductId() {
        Set<Object> objectSet = redisTemplate.opsForZSet().reverseRange("product:readCount",0, 9);
        if (objectSet == null) {
            return Collections.emptyList();
        }
        List<Long> res = new ArrayList<>();
        for (Object o : objectSet) {
            String value = (String) o;
            String[] parts = value.split(":"); // "product:7" -> ["product", "7"]
            res.add(Long.parseLong(parts[1])); // 숫자 부분만 추가
        }
        return res;
    }

    public Boolean isNotViewed (String key) {
        Boolean isNotViewed = redisTemplate.opsForValue().setIfAbsent(key, "viewed");
        return isNotViewed;
    }

    public void clear(){
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    public Double getScore(String key, Long productId){
        return redisTemplate.opsForZSet().score(key, "product:" + productId);
    }

}
