package me.yeon.freship.orders.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.yeon.freship.orders.domain.contant.OrderStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerOrderInfo {

    private String orderCode;
    private OrderStatus status;
    private String productName;
    private int amount;
    private int totalPrice;
    private int delivery;

    @DateTimeFormat(pattern = "yyyy.MM.dd hh:mm:ss")
    private LocalDateTime shippedAt;

    @DateTimeFormat(pattern = "yyyy.MM.dd hh:mm:ss")
    private LocalDateTime createdAt;

    public static CustomerOrderInfo fromOrder(Order order) {
        return new CustomerOrderInfo(
                order.getOrderCode(),
                order.getStatus(),
                order.getProductName(),
                order.getOrderCount(),
                order.getTotalPrice(),
                order.getDeliveryFee(),
                order.getShippedAt(),
                order.getCreatedAt()
        );
    }
}
