package me.yeon.freship.product.controller;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.product.domain.ProductReadCountResponse;
import me.yeon.freship.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v2/products")
public class ProductControllerV2 {

    private final ProductService productService;

    // 조회수를 캐시에 저장한 일반 단건 상품 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductReadCountResponse> findProductV2(@PathVariable Long id){
        ProductReadCountResponse response = productService.findProductV2(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rank")
    public ResponseEntity<List<String>> findProductV3(){
        List<String> results = productService.findTop10ProductId();
        return ResponseEntity.ok(results);
    }

}
