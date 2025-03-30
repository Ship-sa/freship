package me.yeon.freship.product.controller;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.PageCond;
import me.yeon.freship.common.domain.PageInfo;
import me.yeon.freship.common.domain.Response;
import me.yeon.freship.member.domain.AuthMember;
import me.yeon.freship.product.domain.ProductRankResponse;
import me.yeon.freship.product.domain.ProductReadCountResponse;
import me.yeon.freship.product.domain.ProductSearchResponse;
import me.yeon.freship.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/products")
public class ProductControllerV2 {

    private final ProductService productService;

    // Redis Cache 적용
    @GetMapping("/search")
    public ResponseEntity<Response<List<ProductSearchResponse>>> searchProductsWithCache(
            @ModelAttribute PageCond pc,
            @RequestParam String name,
            @AuthenticationPrincipal AuthMember authMember
    ) {
        List<ProductSearchResponse> productList = productService.searchProductsWithCache(name, pc.getPageNum(), pc.getPageSize(), authMember.getId());
        Page<ProductSearchResponse> productPage = new PageImpl<>(productList, PageRequest.of(pc.getPageNum() - 1, pc.getPageSize()), productList.size());
        PageInfo pageInfo = PageInfo.builder()
                .pageNum(pc.getPageNum())
                .pageSize(pc.getPageSize())
                .totalElement(productPage.getTotalElements())
                .totalPage(productPage.getTotalPages())
                .build();
        return ResponseEntity.ok().body(Response.of(productList, pageInfo));
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
                                                                                       @PathVariable Long id) {
        ProductReadCountResponse product = productService.findProductWithReadCount(id, authMember.getId());
        return ResponseEntity.ok().body(Response.of(product));
    }

    // 조회수 기준 상위 10개의 상품 리스트 조회
    @GetMapping("/ranking/read-count")
    public ResponseEntity<Response<List<ProductRankResponse>>> findProductsByReadCount() {
        List<ProductRankResponse> products = productService.findProductsByReadCount();
        return ResponseEntity.ok().body(Response.of(products));
    }

}
