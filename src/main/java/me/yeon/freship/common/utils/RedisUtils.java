package me.yeon.freship.common.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RedisUtils {
    private final RedisTemplate<String, Object> redisTemplate;

    public void setScore(String key, Long id){
        redisTemplate.opsForZSet().add(key, "product:" + id, 1.0);
    }

    public Double incrementScore(String key, Long id){
        return redisTemplate.opsForZSet().incrementScore(key, "product:" + id, 1.0);
    }

    public boolean notExistsKey(String key, Long id){
        Double currentScore = redisTemplate.opsForZSet().score(key, "product:" + id);
        System.out.println(currentScore == null);
        return currentScore == null;
    }

    public List<String> findTop10ProductId() {
        Set<Object> objectSet = redisTemplate.opsForZSet().reverseRange("product:readCount",0, 9);
        if (objectSet == null) {
            return Collections.emptyList(); // 빈 Set 반환
        }
        List<String> res = new ArrayList<>();
        for (Object o : objectSet){
            res.add((String) o);
            System.out.println((String) o);
        }
        return res;
    }

    public void clearAtMidnight(){
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    public Long getData(String key){
        return (Long) redisTemplate.opsForValue().get(key);
    }

    public void deleteData(String key){
        redisTemplate.delete(key);
    }
}
