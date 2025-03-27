package me.yeon.freship.product.service;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.utils.RedisUtils;
import me.yeon.freship.product.domain.Product;
import me.yeon.freship.product.infrastructure.ProductRepository;
import me.yeon.freship.product.domain.ProductReadCountResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RedisUtils redisUtils;

    // 캐싱에 조회수를 저장한 기본 상품 상세 조회
    public ProductReadCountResponse findProductV2(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Not Found"));
        Long readCount = addReadCount(id);
        ProductReadCountResponse productReadCountResponse = ProductReadCountResponse.builder()
                .category(product.getCategory())
                .id(product.getId())
                .readCount(readCount)
                .description(product.getDescription())
                .imgUrl(product.getImgUrl())
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .status(product.getStatus())
                .build();
        return productReadCountResponse;
    }

    // Redis를 이용하여 조회수 증가
    public Long addReadCount(Long id) {
        // Sorted Set 키
        String key = "product:readCount";
        if (redisUtils.notExistsKey(key, id)){
            redisUtils.setScore(key, id);
            return 1L;
        }
        return redisUtils.incrementScore(key, id).longValue();
    }

    public List<String> findTop10ProductId() {
        List<String> results = redisUtils.findTop10ProductId();
        System.out.println(results);
        return results;
    }

    // 자정마다 모든 조회수 삭제
    @Scheduled(cron = "0 0 0 * * ?")
    public void clearRedisAtMidnight() {
        redisUtils.clearAtMidnight();
    }
}
