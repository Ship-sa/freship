package me.yeon.freship.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.yeon.freship.common.domain.Response;
import me.yeon.freship.payment.domain.CheckoutResponse;
import me.yeon.freship.payment.domain.ConfirmResponse;
import me.yeon.freship.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @ResponseStatus(HttpStatus.OK)
    public void fail() {

    }
}
