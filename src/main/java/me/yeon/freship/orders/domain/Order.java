package me.yeon.freship.orders.domain;

import jakarta.persistence.*;
import lombok.Getter;
import me.yeon.freship.common.domain.BaseEntity;
import me.yeon.freship.member.domain.Member;
import me.yeon.freship.orders.domain.contant.OrderStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id @GeneratedValue
    private Long id;

    @Column(length = 10)
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

    // 주문서 생성
    // 출고처리
}
