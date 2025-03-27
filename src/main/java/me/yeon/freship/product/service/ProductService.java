package me.yeon.freship.product.service;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.PageInfo;
import me.yeon.freship.common.domain.Response;
import me.yeon.freship.product.domain.Product;
import me.yeon.freship.product.domain.ProductSearchResponse;
import me.yeon.freship.product.infrastructure.ProductRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ProductRepository productRepository;

    // 캐시를 사용하지 않은 검색 (v1)
    public Response<List<ProductSearchResponse>> searchProductsWithoutCache(String name, int pageNum, int pageSize, Long userId) {
        return searchProducts(name, pageNum, pageSize, userId);
    }

    // 캐시를 사용한 검색 (v2)
    @Cacheable(cacheNames = "popular:search", key = "#name + 'page:' + #pageNum + 'size:' + #pageSize")
    public Response<List<ProductSearchResponse>> searchProductsWithCache(String name, int pageNum, int pageSize, Long userId) {
        return searchProducts(name, pageNum, pageSize, userId);
    }

    // 인기 검색어 조회
    public Response<List<String>> findProductsByPopularSearch() {
        Set<String> popularSearch = redisTemplate.opsForZSet().reverseRange("rank", 0, 9); // 상위 10개 순위 가져오기
        List<String> popularSearchList = new ArrayList<>(popularSearch);
        return Response.of(popularSearchList);
    }

    private Response<List<ProductSearchResponse>> searchProducts(String productName, int pageNum, int pageSize, Long userId) {

        String searchHistoryKey = "user:" + userId + ":search:" + productName;

        if (Boolean.FALSE.equals(redisTemplate.hasKey(searchHistoryKey))) { // 동일한 사용자가 같은 검색어로 검색할 때 횟수 증가 x
            redisTemplate.opsForValue().set(searchHistoryKey, "searched");
            redisTemplate.opsForZSet().incrementScore("rank", productName, 1); // 해당 키워드 검색 횟수 증가
        }

        PageRequest pageable = PageRequest.of(pageNum - 1, pageSize);

        Page<Product> productPage = productRepository.searchByName(pageable, productName);

        PageInfo pageInfo = PageInfo.builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .totalElement(productPage.getTotalElements())
                .totalPage(productPage.getTotalPages())
                .build();

        List<ProductSearchResponse> productSearchResponses = productPage.getContent()
                .stream()
                .map(ProductSearchResponse::fromEntity)
                .toList();

        return Response.of(productSearchResponses, pageInfo);
    }
}
