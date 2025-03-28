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
public class OwnerOrderInfo {

    private Long memberId;
    private String orderCode;
    private OrderStatus status;
    private String productName;
    private String deliveryAddress;
    private int amount;
    private int totalPrice;
    private int deliveryFee;

    @DateTimeFormat(pattern = "yyyy.MM.dd hh:mm:ss")
    private LocalDateTime shippedAt;

    @DateTimeFormat(pattern = "yyyy.MM.dd hh:mm:ss")
    private LocalDateTime createdAt;

    @DateTimeFormat(pattern = "yyyy.MM.dd hh:mm:ss")
    private LocalDateTime updatedAt;

    public static OwnerOrderInfo fromOrder(Order order) {
        return new OwnerOrderInfo(
                order.getId(),
                order.getOrderCode(),
                order.getStatus(),
                order.getProductName(),
                order.getDeliveryAddress(),
                order.getOrderCount(),
                order.getTotalPrice(),
                order.getDeliveryFee(),
                order.getShippedAt(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
