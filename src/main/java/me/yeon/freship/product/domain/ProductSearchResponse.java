package me.yeon.freship.product.domain;

import lombok.Builder;
import lombok.Getter;
import me.yeon.freship.common.domain.PageInfo;

@Getter
@Builder
public class ProductSearchResponse {

    private Long id;
    private String name;
    private Integer price;
    private String imgUrl;
    private PageInfo pageInfo;

    public static ProductSearchResponse fromEntity(Product product, PageInfo pageInfo) {
        return new ProductSearchResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImgUrl(),
                pageInfo
        );
    }
}
