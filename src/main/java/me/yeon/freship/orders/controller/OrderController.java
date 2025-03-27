package me.yeon.freship.orders.controller;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.orders.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public void create() {
        orderService.create(1L, 2, 1L);
    }
}
