package me.yeon.freship.payment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.yeon.freship.common.domain.BaseEntity;
import me.yeon.freship.payment.domain.constant.PaymentStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Table(name = "payment_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PayHistory extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    //    @Column(length = 10)
    private String orderCode;

    @Column(length = 200)
    private String paymentKey;

    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    private PaymentStatus status;

    private LocalDateTime approvedAt;

    private LocalDateTime cancelledAt;

    private PayHistory(
            Long memberId, String orderCode, String paymentKey, int amount,
            PaymentStatus status, LocalDateTime approvedAt, LocalDateTime cancelledAt
    ) {
        this.memberId = memberId;
        this.orderCode = orderCode;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.status = status;
        this.approvedAt = approvedAt;
        this.cancelledAt = cancelledAt;
    }

    public static PayHistory success(Long memberId, String orderCode, String paymentKey, int amount,
                                     String approvedAt) {
        return new PayHistory(memberId, orderCode, paymentKey, amount, PaymentStatus.APPROVED, LocalDateTime.parse(approvedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME), null);
    }

    public static PayHistory pending(Long memberId, String orderCode, String paymentKey, int amount) {
        return new PayHistory(memberId, orderCode, paymentKey, amount, PaymentStatus.PENDING, null, null);
    }


}
