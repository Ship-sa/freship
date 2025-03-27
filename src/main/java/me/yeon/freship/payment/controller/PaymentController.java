package me.yeon.freship.payment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.yeon.freship.common.domain.constant.ErrorCode;
import me.yeon.freship.common.exception.ClientException;
import me.yeon.freship.payment.domain.CheckoutResponse;
import me.yeon.freship.payment.domain.ConfirmResponse;
import me.yeon.freship.payment.infrastructure.client.PaymentHistoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.UUID;

@Slf4j @RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final RestClient restClient = RestClient.create();
    private final PaymentHistoryRepository paymentHistoryRepository;

    @Value("${toss-payments.api-secret-key}")
    private String apiKey;

    @GetMapping("/success")
    @ResponseStatus(HttpStatus.OK)
    public void confirm(@ModelAttribute CheckoutResponse checkoutResponse) throws JsonProcessingException {
        log.info("orderId={} paymentKey={} amount={}", checkoutResponse.getOrderId(), checkoutResponse.getPaymentKey(), checkoutResponse.getAmount());

        /**
         * curl --request POST \
         *   --url https://api.tosspayments.com/v1/payments/confirm \
         *   --header 'Authorization: Basic dGVzdF9za19EbnlScFFXR3JOV3BhMnd4OWJQTDhLd3YxTTlFOg==' \
         *   --header 'Content-Type: application/json' \
         *   --data '{"paymentKey":"5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1","orderId":"a4CWyWY5m89PNh7xJwhk1","amount":1000}'
         */
        // TODO: 넘어온 amount랑 주문서 비교
        // 에러 상황에서의 처리

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


        // 여기서부터 트랜잭션이 될 것임
        // 결제 정보 저장
        paymentHistoryRepository.save(confirmResponse.toSuccessHistory(1L));

        // 주문 정보 저장

        // 결제 정보 저장

    }
}
