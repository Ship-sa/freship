package me.yeon.freship.payment.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckoutRequest {
    private String orderNum;
    private Long customerId;
    private int priceToPay;
    private String itemName;
    private String customerEmail;
    private String customerName;

    @Builder
    public CheckoutRequest(String orderNum, Long customerId, int priceToPay, String itemName, String customerEmail,
                           String customerName) {
        this.orderNum = orderNum;
        this.customerId = customerId;
        this.priceToPay = priceToPay;
        this.itemName = itemName;
        this.customerEmail = customerEmail;
        this.customerName = customerName;
    }
}
