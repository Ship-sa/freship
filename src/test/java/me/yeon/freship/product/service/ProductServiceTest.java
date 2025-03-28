package me.yeon.freship.product.service;

import me.yeon.freship.common.utils.RedisUtils;
import me.yeon.freship.member.domain.Member;
import me.yeon.freship.member.infrastructure.MemberRepository;
import me.yeon.freship.product.domain.Product;
import me.yeon.freship.product.domain.ProductSearchResponse;
import me.yeon.freship.product.infrastructure.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RedisUtils redisUtils;

    @InjectMocks
    private ProductService productService;

    @Test
    void 캐시를_적용하지_않은_상품_검색() {
        // given
        String name = "name";
        int pageNum = 1;
        int pageSize = 10;

        List<Product> productList = List.of(mock(Product.class), mock(Product.class));
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(mock(Member.class)));
        willDoNothing().given(redisUtils).saveSearchHistory(anyLong(), anyString());
        given(productRepository.searchByName(any(Pageable.class), eq(name))).willReturn(productPage);

        // when
        List<ProductSearchResponse> products = productService.searchProductsWithoutCache(name, pageNum, pageSize, 1L);

        // then
        assertNotNull(products);
        assertEquals(productList.size(), products.size());
    }

    @Test
    void 캐시를_적용한_상품_검색() {
        // given
        String name = "name";
        int pageNum = 1;
        int pageSize = 10;

        List<Product> productList = List.of(mock(Product.class), mock(Product.class));
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(mock(Member.class)));
        willDoNothing().given(redisUtils).saveSearchHistory(anyLong(), anyString());
        given(productRepository.searchByName(any(Pageable.class), eq(name))).willReturn(productPage);

        // when
        List<ProductSearchResponse> products = productService.searchProductsWithCache(name, pageNum, pageSize, 1L);

        // then
        assertNotNull(products);
        assertEquals(productList.size(), products.size());
    }

    @Test
    void 인기_검색어_조회() {
        List<String> popularSearch = List.of("1위", "2위");

        given(redisUtils.getTopSearchKeywords()).willReturn(popularSearch);

        List<String> result = productService.findProductsByPopularSearch();

        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
