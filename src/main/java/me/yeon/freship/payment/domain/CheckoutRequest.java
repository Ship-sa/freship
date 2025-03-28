package me.yeon.freship.payment.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.yeon.freship.orders.domain.Order;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckoutRequest {
    private String orderNum;
    private Long customerId;
    private int priceToPay;
    private String itemName;
    private String customerEmail;
    private String customerName;

    public CheckoutRequest(String orderNum, Long customerId, int priceToPay, String itemName, String customerEmail,
                           String customerName) {
        this.orderNum = orderNum;
        this.customerId = customerId;
        this.priceToPay = priceToPay;
        this.itemName = itemName;
        this.customerEmail = customerEmail;
        this.customerName = customerName;
    }

    public static CheckoutRequest fromOrder(Order order) {
        return new CheckoutRequest(
                order.getOrderCode(),
                order.getMember().getId(),
                order.getTotalPrice(),
                order.getProductName(),
                order.getMember().getEmail(),
                order.getMember().getName()
        );
    }
}
