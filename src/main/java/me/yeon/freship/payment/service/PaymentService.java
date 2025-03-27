package me.yeon.freship.payment.service;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.constant.ErrorCode;
import me.yeon.freship.common.exception.ClientException;
import me.yeon.freship.payment.domain.CheckoutResponse;
import me.yeon.freship.payment.domain.ConfirmResponse;
import me.yeon.freship.payment.infrastructure.PaymentHistoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RestClient restClient = RestClient.create();
    private final PaymentHistoryRepository paymentHistoryRepository;

    @Value("${toss-payments.api-secret-key}")
    private String apiKey;

    public ConfirmResponse confirmPayment(CheckoutResponse checkoutResponse) {
        // TODO: 넘어온 amount랑 주문서 비교
        // 에러 상황에서의 처리

        ConfirmResponse confirmResponse = sendConfirmRequest(checkoutResponse);


        // 여기서부터 트랜잭션이 될 것임
        // 결제 정보 저장
        paymentHistoryRepository.save(confirmResponse.toSuccessHistory(1L));

        // 주문 정보 저장

        // 재고 차감

        return confirmResponse;
    }

    private ConfirmResponse sendConfirmRequest(CheckoutResponse checkoutResponse) {
        ConfirmResponse confirmResponse = restClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization",
                        String.format("Basic %s", Base64.getEncoder().encodeToString((apiKey + ":").getBytes())))
                .header("Idempotency-Key", UUID.randomUUID().toString())
                .body(checkoutResponse)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    throw new ClientException(ErrorCode.PAY_CONFIRM_FAILED);
                }))
                .body(ConfirmResponse.class);
        return confirmResponse;
    }
}
