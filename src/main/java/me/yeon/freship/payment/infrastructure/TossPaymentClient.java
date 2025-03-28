package me.yeon.freship.payment.infrastructure;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.orders.domain.Order;
import me.yeon.freship.orders.infrastructure.OrderRepository;
import me.yeon.freship.payment.domain.CheckoutRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class TossPaymentClient {

    private final OrderRepository orderRepository;


    @GetMapping("/payment/checkout")
    public String widget(Model model) {

        Order order = orderRepository.findByIdWithMember(1L)
                .orElseThrow();

        CheckoutRequest request = CheckoutRequest.fromOrder(order);
        model.addAttribute("payRequest", request);

        return "payment/checkout";
    }
}
