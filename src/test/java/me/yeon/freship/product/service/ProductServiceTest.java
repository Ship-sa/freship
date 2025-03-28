package me.yeon.freship.product.service;

import me.yeon.freship.common.utils.RedisUtils;
import me.yeon.freship.product.domain.Product;
import me.yeon.freship.product.domain.ProductRankResponse;
import me.yeon.freship.product.domain.ProductReadCountResponse;
import me.yeon.freship.product.infrastructure.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static me.yeon.freship.product.domain.Category.SNACKS;
import static me.yeon.freship.product.domain.Status.ON_SALE;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RedisUtils redisUtils;

    @InjectMocks
    private ProductService productService;

    @Test
    public void 조회수를_포함하여_상품_상세정보를_조회한다() {
        // given
        Long productId = 1L;
        Long userId = 1L;

        Product product = new Product("포카칩", 5, ON_SALE, SNACKS, 3000,
                "https://example.com/images/포카칩.jpg", "바삭바삭 맛이 좋은 감자칩입니다");
        ReflectionTestUtils.setField(product, "id", productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productService.findReadCount(productId, userId)).thenReturn(10L);

        // when
        ProductReadCountResponse response = productService.findProductV2(productId, userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(product.getId());
        assertThat(response.getReadCount()).isEqualTo(10L);
        assertThat(response.getName()).isEqualTo(product.getName());
        assertThat(response.getQuantity()).isEqualTo(product.getQuantity());
        assertThat(response.getCategory()).isEqualTo(product.getCategory());
        assertThat(response.getPrice()).isEqualTo(product.getPrice());
        assertThat(response.getStatus()).isEqualTo(product.getStatus());
        assertThat(response.getImgUrl()).isEqualTo(product.getImgUrl());
        assertThat(response.getDescription()).isEqualTo(product.getDescription());
    }
    
    @Test
    public void 상품의_조회수가_없는_경우_상품의_조회수를_1로_세팅해준다() {
        // given
        Long productId = 1L;
        String setKey = "product:readCount";

        when(redisUtils.notExistsKey(setKey, productId)).thenReturn(true);

        // when
        Long readCount = productService.addReadCount(productId, setKey);

        // then
        assertThat(readCount).isNotNull();
        assertThat(readCount).isEqualTo(1L);
    }

    @Test
    public void 상품의_조회수가_존재하는_경우_상품의_조회수를_1만큼_증가한다() {
        // given
        Long productId = 1L;
        String setKey = "product:readCount";

        redisUtils.setScore(setKey, productId);
        Long readCount = redisUtils.getScore(setKey, productId);
        when(redisUtils.notExistsKey(setKey, productId)).thenReturn(false);
        when(redisUtils.incrementScore(setKey, productId)).thenReturn(readCount + 1L);

        // when
        Long addedReadCount = productService.addReadCount(productId, setKey);

        // then
        assertThat(addedReadCount).isEqualTo(readCount + 1L);
    }
    
    @Test
    public void 하루동안_방문한_적이_없는_상품을_조회한_경우_조회수가_1_증가한다() {
        // given
        Long productId = 1L;
        Long userId = 1L;
        String setKey = "product:readCount";
        String checkKey = "product:viewed:" + productId + ":" + userId;

        when(redisUtils.isNotViewed(checkKey)).thenReturn(true);
        when(redisUtils.notExistsKey(setKey, productId)).thenReturn(true);

        // when
        Long addedCount = productService.findReadCount(productId, userId);

        // then
        assertThat(addedCount).isNotNull();
        assertThat(addedCount).isEqualTo(1L);
    }

    @Test
    public void 조회수_기준_상위_10개의_상품_리스트를_순위를_포함하여_조회한다() {
        // given
        List<Long> topProductIds = List.of(10L, 5L, 20L, 15L, 1L);

        Product rank1product = new Product("포카칩", 5, ON_SALE, SNACKS, 3000,
                "https://example.com/images/포카칩.jpg", "바삭바삭 맛이 좋은 감자칩입니다");
        ReflectionTestUtils.setField(rank1product, "id", 10L);
        Product rank2product = new Product("수미칩", 5, ON_SALE, SNACKS, 3000,
                "https://example.com/images/수미칩.jpg", "향과 맛이 좋은 감자칩입니다");
        ReflectionTestUtils.setField(rank2product, "id", 5L);
        Product rank3product = new Product("새우칩", 5, ON_SALE, SNACKS, 3000,
                "https://example.com/images/새우칩.jpg", "짭짤한 맛이 좋은 새우칩입니다");
        ReflectionTestUtils.setField(rank3product, "id", 20L);
        Product rank4product = new Product("사과칩", 5, ON_SALE, SNACKS, 3000,
                "https://example.com/images/사과칩.jpg", "달달한 맛이 좋은 사과칩입니다");
        ReflectionTestUtils.setField(rank4product, "id", 15L);
        Product rank5product = new Product("키위칩", 5, ON_SALE, SNACKS, 3000,
                "https://example.com/images/키위칩.jpg", "향긋한 맛이 좋은 키위칩입니다");
        ReflectionTestUtils.setField(rank5product, "id", 1L);
        List<Product> products = new ArrayList<>();
        products.add(rank1product);
        products.add(rank2product);
        products.add(rank3product);
        products.add(rank4product);
        products.add(rank5product);


        when(redisUtils.findTop10ProductId()).thenReturn(topProductIds);
        when(productRepository.findProductsByRank(topProductIds)).thenReturn(products);

        // when
        List<ProductRankResponse> result = productService.findTop10ProductId();

        // then
        assertThat(result).isNotNull();
        assertThat(result.get(0).getId()).isEqualTo(rank1product.getId());
        assertThat(result.get(0).getName()).isEqualTo(rank1product.getName());
    }

}