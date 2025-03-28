package me.yeon.freship.product.service;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.PageInfo;
import me.yeon.freship.common.domain.constant.ErrorCode;
import me.yeon.freship.common.exception.ClientException;
import me.yeon.freship.common.utils.RedisUtils;
import me.yeon.freship.member.infrastructure.MemberRepository;
import me.yeon.freship.product.domain.Product;
import me.yeon.freship.product.domain.ProductSearchResponse;
import me.yeon.freship.product.infrastructure.ProductRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final RedisUtils redisUtils;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    // 캐싱 적용하지 않은 검색 (v1)
    public List<ProductSearchResponse> searchProductsWithoutCache(String name, int pageNum, int pageSize, Long userId) {
        return searchProducts(name, pageNum, pageSize, userId);
    }

    // 캐싱 적용한 검색 (v2)
    @Cacheable(cacheNames = "search", key = "#name + 'page:' + #pageNum + 'size:' + #pageSize")
    public List<ProductSearchResponse> searchProductsWithCache(String name, int pageNum, int pageSize, Long userId) {
        return searchProducts(name, pageNum, pageSize, userId);
    }

    // 인기 검색어 조회
    public List<String> findProductsByPopularSearch() {
        return redisUtils.getTopSearchKeywords();
    }

    private List<ProductSearchResponse> searchProducts(String productName, int pageNum, int pageSize, Long userId) {

        memberRepository.findById(userId).orElseThrow(() -> new ClientException(ErrorCode.NOT_FOUND_MEMBER));

        redisUtils.saveSearchHistory(userId, productName);

        Page<Product> productPage = getProductPage(productName, pageNum, pageSize);
        PageInfo pageInfo = getPageInfo(productName, pageNum, pageSize);

        return productPage.getContent()
                .stream()
                .map(product -> ProductSearchResponse.fromEntity(product, pageInfo))
                .toList();
    }

    private PageInfo getPageInfo(String productName, int pageNum, int pageSize) {
        Page<Product> productPage = getProductPage(productName, pageNum, pageSize);

        return PageInfo.builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .totalElement(productPage.getTotalElements())
                .totalPage(productPage.getTotalPages())
                .build();
    }

    private Page<Product> getProductPage(String productName, int pageNum, int pageSize) {
        PageRequest pageable = PageRequest.of(pageNum - 1, pageSize);
        return productRepository.searchByName(pageable, productName);
    }
}
