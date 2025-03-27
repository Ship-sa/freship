package me.yeon.freship.payment.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ConfirmResponse {

    private String paymentKey;
    private String orderId;

    private int totalAmount;
    private String approvedAt;

    public PayHistory toSuccessHistory(Long memberId) {
        return PayHistory.success(memberId, this.orderId, this.paymentKey, this.totalAmount, this.approvedAt);
    }
}
