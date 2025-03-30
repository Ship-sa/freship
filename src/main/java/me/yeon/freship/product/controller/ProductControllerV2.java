package me.yeon.freship.product.controller;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.Response;
import me.yeon.freship.member.domain.AuthMember;
import me.yeon.freship.product.domain.ProductRankResponse;
import me.yeon.freship.product.domain.ProductReadCountResponse;
import me.yeon.freship.product.domain.ProductSearchResponse;
import me.yeon.freship.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
            @AuthenticationPrincipal AuthMember authMember
    ) {
        List<ProductSearchResponse> products = productService.searchProductsWithCache(name, pageNum, pageSize, authMember.getId());
        return ResponseEntity.ok().body(Response.of(products));
    }

    // 인기 검색어 조회
    @GetMapping("/ranking/keyword")
    public ResponseEntity<Response<List<String>>> findProductsByKeyword() {
        List<String> keywords = productService.findProductsByPopularSearch();
        return ResponseEntity.ok().body(Response.of(keywords));
    }

    // 캐시에 조회수를 저장한 단건 상품 조회
    @GetMapping("/{id}")
    public ResponseEntity<Response<ProductReadCountResponse>> findProductWithReadCount(@AuthenticationPrincipal AuthMember authMember,
                                                                                       @PathVariable Long id){
        ProductReadCountResponse product = productService.findProductWithReadCount(id, authMember.getId());
        return ResponseEntity.ok().body(Response.of(product));
    }

    // 조회수 기준 상위 10개의 상품 리스트 조회
    @GetMapping("/ranking/read-count")
    public ResponseEntity<Response<List<ProductRankResponse>>> findProductsByReadCount(){
        List<ProductRankResponse> products = productService.findProductsByReadCount();
        return ResponseEntity.ok().body(Response.of(products));
    }

}
