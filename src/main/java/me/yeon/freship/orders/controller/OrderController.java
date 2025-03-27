package me.yeon.freship.orders.controller;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.PageCond;
import me.yeon.freship.common.domain.PageInfo;
import me.yeon.freship.common.domain.Response;
import me.yeon.freship.orders.domain.CreateRequest;
import me.yeon.freship.orders.domain.CustomerOrderInfo;
import me.yeon.freship.orders.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<Response<Void>> create(@RequestBody CreateRequest req) {

        Long orderId = orderService.create(req.getMemberId(), req.getOrderAmount(), req.getProductId());
        return ResponseEntity.created(URI.create("/orders/" + orderId)).build();
    }

    // 사용자용 조회 api
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Response<CustomerOrderInfo>> findOneForMember(
            @PathVariable(name = "orderId") Long orderId
    ) {
        return ResponseEntity.ok(
                Response.of(orderService.findOneByCustomer(orderId)));
    }

    @GetMapping("/orders")
    public ResponseEntity<Response<List<CustomerOrderInfo>>> findAllForMember(
            @ModelAttribute PageCond pc
    ) {
        Page<CustomerOrderInfo> resultPage = orderService.findAllByCustomer(pc.getPageNum(), pc.getPageSize());
        PageInfo pageInfo = PageInfo.builder()
                .pageNum(pc.getPageNum())
                .pageSize(pc.getPageSize())
                .totalElement(resultPage.getTotalElements())
                .totalPage(resultPage.getTotalPages())
                .build();

        return ResponseEntity.ok(
                Response.of(resultPage.getContent(), pageInfo));
    }
}
