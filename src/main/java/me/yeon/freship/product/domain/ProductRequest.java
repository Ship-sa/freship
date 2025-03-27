package me.yeon.freship.product.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ProductRequest {

    @NotBlank(message = "상품명을 입력하세요.")
    private String name;

    @NotBlank(message = "재고를 입력하세요.")
    private Integer quantity;

    @NotBlank(message = "상태를 입력하세요.")
    private Status status;

    @NotBlank(message = "카테고리를 입력하세요.")
    private Category category;

    @NotBlank(message = "가격을 입력하세요.")
    private Integer price;

    @NotBlank(message = "이미지 주소를 입력하세요.")
    private String imgUrl;

    private String description;

}
