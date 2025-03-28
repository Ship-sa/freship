package me.yeon.freship.common.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate<String, String> redisTemplate;

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
        Set<String> popularSearch = redisTemplate.opsForZSet().reverseRange("rank", 0, 9);
        return popularSearch != null ? new ArrayList<>(popularSearch) : new ArrayList<>();
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
    }
}
