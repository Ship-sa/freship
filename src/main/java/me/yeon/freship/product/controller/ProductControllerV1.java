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
@RequestMapping("/v1/products")
public class ProductControllerV1 {

    private final ProductService productService;

    // TODO: user 쪽 구현 되면 userId 받아오는 걸로 수정하기
    @GetMapping("/search")
    public ResponseEntity<Response<List<ProductSearchResponse>>> searchProductsWithoutCache(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam String name,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(productService.searchProductsWithoutCache(name, pageNum, pageSize, userId));
    }
}
