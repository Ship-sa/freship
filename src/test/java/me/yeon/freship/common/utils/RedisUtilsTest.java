package me.yeon.freship.common.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RedisUtilsTest {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @AfterEach
    public void clear() {
        redisTemplate.delete("product:readCount");
    }

    @Test
    public void 상품_조회수가_1로_저장된다() {
        // given
        String setKey = "product:readCount";
        Long productId = 1L;

        // when
        redisUtils.setReadCount(productId);

        // then
        Double score = redisTemplate.opsForZSet().score(setKey, "product:" + productId);
        assertThat(score).isNotNull();
        assertThat(score).isEqualTo(1.0);
    }

    @Test
    public void 상품_조회수를_1만큼_증가한다() {
        // given
        String setKey = "product:readCount";
        Long productId = 1L;
        redisUtils.setReadCount(productId);

        // when
        redisUtils.addReadCount(productId);

        // then
        Double score = redisTemplate.opsForZSet().score(setKey, "product:" + productId);
        assertThat(score).isNotNull();
        assertThat(score).isEqualTo(2.0);
    }

    @Test
    public void 상품의_조회수_데이터의_존재여부를_확인한다() {
        // given
        Long productId = 1L;

        // when
        Boolean isNotExist = redisUtils.notExistsReadCount(productId);

        // then
        assertThat(isNotExist).isNotNull();
        assertThat(isNotExist).isEqualTo(true);
    }

    @Test
    public void 조회수_상위_10개의_상품ID_리스트를_조회한다() {
        // given
        String setKey = "product:readCount";
        Long top1readCountId = 9L;
        Long top2readCountId = 0L;
        for (Long i = 0L; i < 20; i++) {
            redisUtils.setReadCount(i);
        }
        for (Long i = 0L; i < 10; i++) {
            for (Long j = 0L; j <= i; j++) {
                redisUtils.addReadCount(i);
            }
        }

        // when
        List<Long> productIdList = redisUtils.findProductIds();
    
        // then
        assertThat(productIdList).isNotNull();
        assertThat(productIdList.get(0)).isEqualTo(top1readCountId);
        assertThat(productIdList.get(9)).isEqualTo(top2readCountId);
    }
    
    @Test
    public void 조회수_상위_10개의_상품ID가_Redis에_존재하지_않으면_emptyList를_반환한다() {
        // given & when
        List<Long> productIdList = redisUtils.findProductIds();

        // then
        assertThat(productIdList).isNotNull();
        assertThat(productIdList.size()).isEqualTo(0);
    }

    @Test
    public void 사용자가_한_번도_해당_상품을_조회하지_않았다면_TRUE를_반환한다() {
        // given
        Long productId = 1L;
        Long userId = 1L;

        // when
        Boolean isNotViewed = redisUtils.isNotViewed(productId, userId);

        // then
        assertThat(isNotViewed).isNotNull();
        assertThat(isNotViewed).isEqualTo(TRUE);
    }

    @Test
    public void Redis에_있는_모든_데이터를_삭제한다() {
        // given
        String setKey = "product:readCount";
        Long productId = 1L;
        redisUtils.setReadCount(productId);

        // when
        redisUtils.clearCache();
        Double score = redisTemplate.opsForZSet().score(setKey, "product:" + productId);

        // then
        assertThat(score).isNull();
    }

    @Test
    public void 상품의_조회수를_조회한다() {
        // given
        String setKey = "product:readCount";
        Long productId = 1L;
        redisUtils.setReadCount(productId);

        // when
        Long readCount = redisUtils.getReadCount(productId);

        // then
        assertThat(readCount).isNotNull();
        assertThat(readCount).isEqualTo(1L);
    }

}