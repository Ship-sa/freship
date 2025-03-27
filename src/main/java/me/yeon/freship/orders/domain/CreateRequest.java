package me.yeon.freship.orders.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateRequest {
    private Long memberId;
    private int orderAmount;
    private Long productId;
}
