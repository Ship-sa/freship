package me.yeon.freship.product.service;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.utils.RedisUtils;
import me.yeon.freship.product.domain.Product;
import me.yeon.freship.product.infrastructure.ProductRepository;
import me.yeon.freship.product.domain.ProductReadCountResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RedisUtils redisUtils;

    // 조회수 없는 기본 상품 상세 조회
    public ProductReadCountResponse findProductV1(Long id) {
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
        String key = "product:readCount:"+id;
        if (redisUtils.existsKey(key)){
             return redisUtils.incrementData(key);
        }
        redisUtils.setData(key, 1L, 60L);
        return 1L;
    }

}
