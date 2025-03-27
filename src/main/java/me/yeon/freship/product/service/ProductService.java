package me.yeon.freship.product.service;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.utils.RedisUtils;
import me.yeon.freship.product.domain.Product;
import me.yeon.freship.product.domain.ProductRankResponse;
import me.yeon.freship.product.infrastructure.ProductRepository;
import me.yeon.freship.product.domain.ProductReadCountResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                .id(product.getId())
                .readCount(readCount)
                .name(product.getName())
                .quantity(product.getQuantity())
                .category(product.getCategory())
                .price(product.getPrice())
                .status(product.getStatus())
                .price(product.getPrice())
                .imgUrl(product.getImgUrl())
                .description(product.getDescription())
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

    // 조회수 기준 상위 10개의 상품 리스트 조회하기
    @Cacheable(cacheNames = "getRanks", key = "'products:rank'", cacheManager = "productCacheManager")
    public List<ProductRankResponse> findTop10ProductId() {
        List<Long> idList = redisUtils.findTop10ProductId();
        List<Product> products = productRepository.findProductsByRank(idList);

        // 정렬
        Map<Long, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < idList.size(); i++) {
            orderMap.put(idList.get(i), i);
        }
        products.sort(Comparator.comparingInt(p -> orderMap.get(p.getId())));

        List<ProductRankResponse> readCountResponses = ProductRankResponse.toProductRankResponseList(products);
        return readCountResponses;
    }

    // 자정마다 모든 조회수 삭제
    @Scheduled(cron = "0 0 0 * * ?")
    public void clearRedisAtMidnight() {
        redisUtils.clearAtMidnight();
    }
}
