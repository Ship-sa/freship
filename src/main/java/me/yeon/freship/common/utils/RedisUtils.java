package me.yeon.freship.common.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    // 사용자 검색 기록 저장 (동일한 검색어 중복 방지)
    public void saveSearchHistory(Long userId, String searchKeyword) {
        String searchHistoryKey = "user:" + userId + ":search:" + searchKeyword;
        if (Boolean.FALSE.equals(redisTemplate.hasKey(searchHistoryKey))) {
            redisTemplate.opsForValue().set(searchHistoryKey, "searched");
            incrementSearchRank(searchKeyword);
        }
    }

    // 검색어 순위 증가
    private void incrementSearchRank(String searchKeyword) {
        redisTemplate.opsForZSet().incrementScore("rank", searchKeyword, 1);
    }

    // 인기 검색어 조회 (상위 10개)
    public List<String> getTopSearchKeywords() {
        Set<String> popularSearch = redisTemplate.opsForZSet()
                .reverseRange("rank", 0, 9)
                .stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
        return popularSearch != null ? new ArrayList<>(popularSearch) : new ArrayList<>();
    }

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

    // 자정에 캐시 리셋
    @Scheduled(cron = "0 0 0 * * ?")
    public void clearCache() {

        Set<String> cacheKeys = redisTemplate.keys("search*");
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

        Set<String> visitorKeys = redisTemplate.keys("product*");
        if (visitorKeys != null && !visitorKeys.isEmpty()) {
            redisTemplate.delete(visitorKeys);
        }

        if (Boolean.TRUE.equals(redisTemplate.hasKey("product:readCount"))) {
            redisTemplate.opsForZSet().removeRange("product:readCount", 0, -1);
        }
    }

}
