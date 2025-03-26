package me.yeon.freship.store.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class StoreResponse {

    private final Long id;
    private final String name;
    private final String bizRegNum;
    private final String address;

    public static StoreResponse fromEntity(Store store) {
        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .bizRegNum(store.getBizRegNum())
                .address(store.getAddress())
                .build();
    }
}
