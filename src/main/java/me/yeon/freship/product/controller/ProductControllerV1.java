package me.yeon.freship.product.controller;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.Response;
import me.yeon.freship.member.domain.AuthMember;
import me.yeon.freship.product.domain.ProductSearchResponse;
import me.yeon.freship.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/search")
    public ResponseEntity<Response<List<ProductSearchResponse>>> searchProductsWithoutCache(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam String name,
            @AuthenticationPrincipal AuthMember authMember
    ) {
        List<ProductSearchResponse> products = productService.searchProductsWithoutCache(name, pageNum, pageSize, authMember.getId());
        return ResponseEntity.ok().body(Response.of(products));
    }
}
