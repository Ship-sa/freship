package me.yeon.freship.product.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ProductRequest {

    @NotBlank(message = "상품명을 입력하세요.")
    private String name;

    @NotNull(message = "재고를 입력하세요.")
    @Min(value = 0, message = "재고를 0개 이상으로 입력하세요.")
    private Integer quantity;

    @NotNull(message = "상태를 입력하세요.")
    private Status status;

    @NotNull(message = "카테고리를 입력하세요.")
    private Category category;

    @NotNull(message = "가격을 입력하세요.")
    @Min(value = 0, message = "가격을 0원 이상으로 입력하세요.")
    private Integer price;

    @NotBlank(message = "이미지 주소를 입력하세요.")
    private String imgUrl;

    private String description;

}
