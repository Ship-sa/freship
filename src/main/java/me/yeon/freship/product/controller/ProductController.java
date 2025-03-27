package me.yeon.freship.product.controller;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.product.domain.ProductReadCountResponse;
import me.yeon.freship.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v2/products")
public class ProductController {

    private final ProductService productService;

    // 조회수를 포함하지 않은 일반 단건 상품 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductReadCountResponse> findProduct(@PathVariable Long id){
        ProductReadCountResponse response = productService.findProductV1(id);
        return ResponseEntity.ok(response);
    }


}
