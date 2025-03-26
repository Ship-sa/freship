package me.yeon.freship.product.controller;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.Response;
import me.yeon.freship.product.domain.ProductSearchResponse;
import me.yeon.freship.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/v1/products/search")
    public ResponseEntity<Response<List<ProductSearchResponse>>> searchProductsWithoutCache(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam String name
    ) {
        return ResponseEntity.ok(productService.searchProductsWithoutCache(name, pageNum, pageSize));
    }

    // In-memory Cache 적용(Local Memory)
    @GetMapping("/v2/products/search")
    public ResponseEntity<Response<List<ProductSearchResponse>>> searchProductsWithCache(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam String name
    ) {
        return ResponseEntity.ok(productService.searchProductsWithCache(name, pageNum, pageSize));
    }
}
