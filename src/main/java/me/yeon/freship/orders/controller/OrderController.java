package me.yeon.freship.orders.controller;

import lombok.RequiredArgsConstructor;
import me.yeon.freship.common.domain.PageCond;
import me.yeon.freship.common.domain.PageInfo;
import me.yeon.freship.common.domain.Response;
import me.yeon.freship.member.domain.AuthMember;
import me.yeon.freship.member.domain.MemberRole;
import me.yeon.freship.orders.domain.CreateRequest;
import me.yeon.freship.orders.domain.CustomerOrderInfo;
import me.yeon.freship.orders.domain.OwnerOrderInfo;
import me.yeon.freship.orders.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<Response<Void>> create(
            @RequestBody CreateRequest req,
            @AuthenticationPrincipal AuthMember authMember
    ) throws InterruptedException {

        Long orderId = orderService.create(authMember.getId(), req.getOrderAmount(), req.getProductId());
        return ResponseEntity.created(URI.create("/orders/" + orderId)).build();
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<Response<Map<String, Long>>> cancel(
            @PathVariable("orderId") Long orderId,
            @AuthenticationPrincipal AuthMember authMember
    ) {
        Long cancelledId = orderService.cancel(orderId, authMember.getId());
        return ResponseEntity.ok(
                Response.of(Map.of("id", cancelledId))
        );
    }

    // 사용자용 조회 api
    @GetMapping("/orders")
    public ResponseEntity<Response<List<CustomerOrderInfo>>> findAllForMember(
            @ModelAttribute PageCond pc,
            @RequestParam(value = "memberId", defaultValue = "1") Long memberId //TODO: authMember로 변경
    ) {
        Page<CustomerOrderInfo> resultPage = orderService.findAllByCustomer(memberId, pc.getPageNum(), pc.getPageSize());
        PageInfo pageInfo = PageInfo.builder()
                .pageNum(pc.getPageNum())
                .pageSize(pc.getPageSize())
                .totalElement(resultPage.getTotalElements())
                .totalPage(resultPage.getTotalPages())
                .build();

        return ResponseEntity.ok(
                Response.of(resultPage.getContent(), pageInfo));
    }

    @Secured(MemberRole.Authority.MEMBER)
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Response<CustomerOrderInfo>> findOneForMember(
            @PathVariable(name = "orderId") Long orderId,
            @AuthenticationPrincipal AuthMember authMember
    ) {
        return ResponseEntity.ok(
                Response.of(orderService.findOneByCustomer(authMember.getId(), orderId)));
    }

    // 배송출발은 Owner만 가능
    @PutMapping("/manage/orders/{orderId}/delivery")
    public ResponseEntity<Response<Void>> startDelivery(
            @PathVariable("orderId") Long orderId,
            @AuthenticationPrincipal AuthMember authMember
    ) {
        orderService.startDelivery(orderId, authMember.getId());
        return ResponseEntity.ok().build();
    }

    // 사장용 조회 API
    @GetMapping("/manage/orders")
    public ResponseEntity<Response<List<OwnerOrderInfo>>> findAllForOwner(
            @ModelAttribute PageCond pc,
            @AuthenticationPrincipal AuthMember authMember
    ) {
        Page<OwnerOrderInfo> resultPage = orderService.findAllByOwner(authMember.getId(), pc.getPageNum(), pc.getPageSize());
        PageInfo pageInfo = PageInfo.builder()
                .pageNum(pc.getPageNum())
                .pageSize(pc.getPageSize())
                .totalElement(resultPage.getTotalElements())
                .totalPage(resultPage.getTotalPages())
                .build();
        return ResponseEntity.ok(Response.of(resultPage.getContent(), pageInfo));
    }

}
