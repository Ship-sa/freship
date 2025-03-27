package me.yeon.freship.payment.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CheckoutResponse {
    private String orderId;
    private String paymentKey;
    private int amount;
}
