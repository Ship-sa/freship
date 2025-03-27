package me.yeon.freship.product.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProductRankResponse {

    private final Long id;
    private final Long rank;
    private final String name;
    private final Integer quantity;
    private final Status status;
    private final Category category;
    private final Integer price;
    private final String imgUrl;
    private final String description;

    @Builder
    public ProductRankResponse(Long id, Long rank, String name, Integer quantity, Status status, Category category, Integer price, String imgUrl, String description) {
        this.id = id;
        this.rank = rank;
        this.name = name;
        this.quantity = quantity;
        this.status = status;
        this.category = category;
        this.price = price;
        this.imgUrl = imgUrl;
        this.description = description;
    }

    public static List<ProductRankResponse> toProductRankResponseList(List<Product> products){
        List<ProductRankResponse> readCountResponses = new ArrayList<>();
        Long rank = 1L;
        for (Product product : products) {
            ProductRankResponse productReadCountResponse = ProductRankResponse.builder()
                    .id(product.getId())
                    .rank(rank++)
                    .name(product.getName())
                    .quantity(product.getQuantity())
                    .category(product.getCategory())
                    .price(product.getPrice())
                    .status(product.getStatus())
                    .price(product.getPrice())
                    .imgUrl(product.getImgUrl())
                    .description(product.getDescription())
                    .build();

            readCountResponses.add(productReadCountResponse);
        }
        return readCountResponses;
    }
}
