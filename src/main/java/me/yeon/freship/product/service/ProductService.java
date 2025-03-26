package me.yeon.freship.product.service;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.PageInfo;
import me.yeon.freship.common.domain.Response;
import me.yeon.freship.product.domain.Product;
import me.yeon.freship.product.domain.ProductSearchResponse;
import me.yeon.freship.product.infrastructure.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public Response<List<ProductSearchResponse>> searchProducts(String name, int pageNum, int pageSize) {
        PageRequest pageable = PageRequest.of(pageNum - 1, pageSize);

        Page<Product> productPage = productRepository.searchByName(pageable, name);

        PageInfo pageInfo = PageInfo.builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .totalElement(productPage.getTotalElements())
                .totalPage(productPage.getTotalPages())
                .build();

        List<ProductSearchResponse> productSearchResponses = productPage.getContent()
                .stream()
                .map(ProductSearchResponse::fromEntity)
                .toList();

        return Response.of(productSearchResponses, pageInfo);
    }
}
