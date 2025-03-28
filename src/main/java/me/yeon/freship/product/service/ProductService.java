package me.yeon.freship.product.service;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.exception.ClientException;
import me.yeon.freship.common.utils.RedisUtils;
import me.yeon.freship.product.domain.Product;
import me.yeon.freship.product.domain.ProductRankResponse;
import me.yeon.freship.product.infrastructure.ProductRepository;
import me.yeon.freship.product.domain.ProductReadCountResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.yeon.freship.common.domain.constant.ErrorCode.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RedisUtils redisUtils;

    // 캐시에 조회수를 저장한 단건 상품 조회
    @Transactional
    public ProductReadCountResponse findProductWithReadCount(Long id, Long userId) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ClientException(PRODUCT_NOT_FOUND));
        Long readCount = findReadCount(product.getId(), userId);
        ProductReadCountResponse productReadCountResponse =
                ProductReadCountResponse.builder()
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

    // 어뷰징 검증, 24시간 이내에 방문했다면 기존 조회수 조회, 아니라면 조회수 증가
    public Long findReadCount(Long productId, Long userId) {
        Boolean isNotViewed = redisUtils.isNotViewed(productId, userId);
        if (Boolean.TRUE.equals(isNotViewed)) {
            return addReadCount(productId);
        }
        return redisUtils.getReadCount(productId);
    }

    // 조회수가 없는 경우엔 1로 초기화, 존재하는 경우엔 조회수를 1만큼 증가
    public Long addReadCount(Long productId) {
        if (redisUtils.notExistsReadCount(productId)){
            redisUtils.setReadCount(productId);
            return 1L;
        }
        return redisUtils.addReadCount(productId);
    }

    // 조회수 기준 상위 10개의 상품 리스트 조회하기
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "rank", key = "'products:rank'", cacheManager = "productCacheManager")
    public List<ProductRankResponse> findProductsByReadCount() {
        List<Long> idList = redisUtils.findProductIds();
        List<Product> products = productRepository.findProductsByRank(idList);

        // 순위별로 정렬
        products.sort(Comparator.comparingInt(p -> idList.indexOf(p.getId())));

        List<ProductRankResponse> readCountResponses = ProductRankResponse.toProductRankResponseList(products);
        return readCountResponses;
    }

}
