package me.yeon.freship.product.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProductReadCountResponse {

    private final Long id;
    private final Long readCount;
    private final String name;
    private final Integer quantity;
    private final Status status;
    private final Category category;
    private final Integer price;
    private final String imgUrl;
    private final String description;

    @Builder
    public ProductReadCountResponse(Long id, Long readCount, String name, Integer quantity, Status status, Category category, Integer price, String imgUrl, String description) {
        this.id = id;
        this.readCount = readCount;
        this.name = name;
        this.quantity = quantity;
        this.status = status;
        this.category = category;
        this.price = price;
        this.imgUrl = imgUrl;
        this.description = description;
    }

}
