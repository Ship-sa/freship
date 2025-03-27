package me.yeon.freship.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.PageInfo;
import me.yeon.freship.common.domain.Response;
import me.yeon.freship.member.domain.AuthMember;
import me.yeon.freship.product.domain.Category;
import me.yeon.freship.product.domain.ProductRequest;
import me.yeon.freship.product.domain.ProductResponse;
import me.yeon.freship.product.domain.ProductSearchResponse;
import me.yeon.freship.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/products")
public class ProductControllerV1 {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Response<ProductResponse>> saveProduct(
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable Long storeId,
            @Valid @RequestBody ProductRequest request
    ) {
        Response<ProductResponse> response = productService.saveProduct(authMember, storeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Response<List<ProductResponse>>> findProducts(
            @RequestParam(value = "category", required = false) Category category,
            @RequestParam PageInfo pageInfo
    ) {
        Page<ProductResponse> page = productService.findProducts(category, pageInfo);
        return ResponseEntity.ok(Response.of(page.getContent(), pageInfo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<ProductResponse>> findProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<ProductResponse>> updateProduct(
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable Long id,
            @RequestBody ProductRequest request
    ) {
        return ResponseEntity.ok(productService.updateProduct(authMember, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable Long id
    ) {
        productService.deleteProduct(authMember, id);
        return ResponseEntity.noContent().build();
    }

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
