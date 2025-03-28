package me.yeon.freship.common.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor
public class RedisUtils {
    private final RedisTemplate<String, Object> redisTemplate;

    public void setReadCount(Long id){
        redisTemplate.opsForZSet().add("product:readCount", "product:" + id, 1);
    }

    public Long getReadCount(Long productId){
        return redisTemplate.opsForZSet().score("product:readCount", "product:" + productId).longValue();
    }

    public Long addReadCount(Long productId){
        return redisTemplate.opsForZSet().incrementScore("product:readCount", "product:" + productId, 1).longValue();
    }

    public boolean notExistsReadCount(Long productId){
        // 값이 존재하지 않는다면 null 반환
        Double currentReadCount = redisTemplate.opsForZSet().score("product:readCount", "product:" + productId);
        return currentReadCount == null;
    }

    // 조회수 상위 10개의 상품 id 리스트 조회
    public List<Long> findProductIds() {
        Set<Object> objectSet = redisTemplate.opsForZSet().reverseRange("product:readCount",0, 9);
        if (objectSet == null) {
            return Collections.emptyList();
        }

        //"product:7" -> ["product", "7"] -> ["7"]로 변환
        List<Long> productIdList = new ArrayList<>();
        for (Object o : objectSet) {
            String value = (String) o;
            String[] parts = value.split(":");
            productIdList.add(Long.parseLong(parts[1]));
        }
        return productIdList;
    }

    public Boolean isNotViewed (Long productId, Long userId) {
        Boolean isNotViewed = redisTemplate.opsForValue().setIfAbsent("product:" + productId + ":user:" + userId, "viewed");
        return isNotViewed;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void clearCache(){
        Set<String> visitorKeys = redisTemplate.keys("product*");
        if (visitorKeys != null && !visitorKeys.isEmpty()) {
            redisTemplate.delete(visitorKeys);
        }

        if (Boolean.TRUE.equals(redisTemplate.hasKey("product:readCount"))) {
            redisTemplate.opsForZSet().removeRange("product:readCount", 0, -1);
        }
    }

}
