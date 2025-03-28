package me.yeon.freship.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.PageInfo;
import me.yeon.freship.common.domain.Response;
import me.yeon.freship.member.domain.AuthMember;
import me.yeon.freship.product.domain.Category;
import me.yeon.freship.product.domain.ProductRequest;
import me.yeon.freship.product.domain.ProductResponse;
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

    @PostMapping("/{storeId}")
    public ResponseEntity<Response<ProductResponse>> saveProduct(
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable Long storeId,
            @Valid @RequestBody ProductRequest request
    ) {
        ProductResponse response = productService.saveProduct(authMember, storeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.of(response));
    }

    @GetMapping
    public ResponseEntity<Response<List<ProductResponse>>> findProducts(
            @RequestParam(value = "category", required = false) Category category,
            @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
    ) {
        PageInfo page = new PageInfo(pageNum, pageSize, 0, 0);
        Page<ProductResponse> responsePage = productService.findProducts(category, page);
        page = PageInfo.of(responsePage, pageNum, pageSize);

        return ResponseEntity.ok(Response.of(responsePage.getContent(), page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<ProductResponse>> findProductById(@PathVariable Long id) {
        ProductResponse response = productService.findProductById(id);
        return ResponseEntity.ok(Response.of(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<ProductResponse>> updateProduct(
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable Long id,
            @RequestBody ProductRequest request
    ) {
        ProductResponse response = productService.updateProduct(authMember, id, request);
        return ResponseEntity.ok(Response.of(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable Long id
    ) {
        productService.deleteProduct(authMember, id);
        return ResponseEntity.noContent().build();
    }

}
