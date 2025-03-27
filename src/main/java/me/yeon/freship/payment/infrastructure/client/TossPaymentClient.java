package me.yeon.freship.payment.infrastructure.client;

import me.yeon.freship.payment.domain.CheckoutRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

@Controller
public class TossPaymentClient {

    @GetMapping("/payment/checkout")
    public String widget(Model model) {

        model.addAttribute(
                "payRequest",
                CheckoutRequest.builder()
                        .customerId(1L)
                        .orderNum(UUID.randomUUID().toString())
                        .customerEmail("example@example.com")
                        .customerName("example")
                        .itemName("투수 티셔츠")
                        .priceToPay(60000)
                        .build());

        return "payment/checkout";
    }
}
