package me.yeon.freship.orders.domain.contant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    CANCEL("결제취소"),
    PAY_COMPLETE("결제완료"),
    DELI_PROVISION("상품준비중"),
    DELI_START("배송지시"),
    DELI_PROGRESS("배송시작"),
    DELI_DONE("배송완료");

    private final String kr;
}
