package me.yeon.freship.store.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class StoreRequest {

    @NotBlank(message = "상호명을 입력하세요.")
    private String name;

    @NotBlank(message = "사업자번호를 입력하세요")
    private String bizRegNum;

    @NotBlank(message = "사업장주소를 입력하세요.")
    private String address;

}
