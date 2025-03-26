package me.yeon.freship.product.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductSearchResponse {

    private Long id;
    private String name;
    private Integer price;
    private String imgUrl;

    public static ProductSearchResponse fromEntity(Product product) {
        return new ProductSearchResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImgUrl()
        );
    }
}
