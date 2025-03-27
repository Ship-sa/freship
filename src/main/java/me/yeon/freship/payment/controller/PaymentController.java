package me.yeon.freship.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.yeon.freship.common.domain.Response;
import me.yeon.freship.payment.domain.CheckoutFailResponse;
import me.yeon.freship.payment.domain.CheckoutResponse;
import me.yeon.freship.payment.domain.ConfirmResponse;
import me.yeon.freship.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;


    @GetMapping("/success")
    public ResponseEntity<Response<ConfirmResponse>> confirm(@ModelAttribute CheckoutResponse checkoutResponse) {
        return ResponseEntity.ok(
                Response.of(paymentService.confirmPayment(checkoutResponse))
        );
    }

    @GetMapping("/fail")
    public ResponseEntity<Response<CheckoutFailResponse>> confirmFail(
            @ModelAttribute CheckoutFailResponse checkoutFailResponse
    ) {
        // redirect로 넘어와 PG측 status code 구분불가, 422로 넘겨줌
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Response.of(checkoutFailResponse));
    }
}
