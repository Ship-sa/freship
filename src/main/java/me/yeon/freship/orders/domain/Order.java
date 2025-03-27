package me.yeon.freship.orders.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.yeon.freship.common.domain.BaseEntity;
import me.yeon.freship.member.domain.Member;
import me.yeon.freship.orders.domain.contant.OrderStatus;
import me.yeon.freship.product.domain.Product;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    public static final int DELIVERY_FEE = 3000;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 11)
    private String orderCode;

    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    private OrderStatus status;

    @Column(length = 30)
    private String productName;

    private int orderCount;

    private int totalPrice;

    private int deliveryFee;

    private LocalDateTime shippedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Order(
            String orderCode, OrderStatus status, String productName,
            int orderCount, int totalPrice, int deliveryFee, Member member
    ) {
        this.orderCode = orderCode;
        this.status = status;
        this.productName = productName;
        this.orderCount = orderCount;
        this.totalPrice = totalPrice;
        this.deliveryFee = deliveryFee;
        this.member = member;
    }

    // 주문서 생성
    public static Order newOrder(String orderCode, Member member, Product product, int amount) {
        return new Order(
                orderCode, OrderStatus.PENDING, product.getName(),
                amount, product.getPrice() * amount, DELIVERY_FEE,
                member
        );
    }

    // 출고처리
    public void startDelivery(LocalDateTime shippedAt) {
        this.status = OrderStatus.DELI_PROGRESS;
        this.shippedAt = shippedAt;
    }

    public void changeStatus(OrderStatus status) {
        this.status = status;
    }
}
