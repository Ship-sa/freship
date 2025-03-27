package me.yeon.freship.product.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class ProductResponse {

    private final Long id;
    private final String storeName;
    private final String name;
    private final Integer quantity;
    private final Status status;
    private final Category category;
    private final Integer price;
    private final String imgUrl;
    private final String description;

    public static ProductResponse fromEntity(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .storeName(product.getStore().getName())
                .name(product.getName())
                .quantity(product.getQuantity())
                .status(product.getStatus())
                .category(product.getCategory())
                .price(product.getPrice())
                .imgUrl(product.getImgUrl())
                .description(product.getDescription())
                .build();
    }

}
