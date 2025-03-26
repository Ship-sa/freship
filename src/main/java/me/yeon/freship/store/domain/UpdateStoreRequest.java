package me.yeon.freship.store.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateStoreRequest {

    private String name;
    private String address;

}
