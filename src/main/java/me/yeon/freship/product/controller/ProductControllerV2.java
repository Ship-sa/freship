package me.yeon.freship.product.controller;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.Response;
import me.yeon.freship.product.domain.ProductSearchResponse;
import me.yeon.freship.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/products")
public class ProductControllerV2 {

    private final ProductService productService;

    // In-memory Cache 적용(Local Memory)
    @GetMapping("/search")
    public ResponseEntity<Response<List<ProductSearchResponse>>> searchProductsWithCache(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam String name,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(productService.searchProductsWithCache(name, pageNum, pageSize, userId));
    }

    // 인기 검색어 조회
    @GetMapping("/ranking/keyword")
    public ResponseEntity<Response<List<String>>> findProductsByKeyword() {
        return ResponseEntity.ok(productService.findProductsByPopularSearch());
    }
}
