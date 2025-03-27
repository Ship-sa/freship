package me.yeon.freship.orders.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.yeon.freship.common.domain.constant.ErrorCode;
import me.yeon.freship.common.exception.ClientException;
import me.yeon.freship.member.domain.Member;
import me.yeon.freship.member.infrastructure.MemberRepository;
import me.yeon.freship.orders.domain.CustomerOrderInfo;
import me.yeon.freship.orders.domain.Order;
import me.yeon.freship.orders.domain.OwnerOrderInfo;
import me.yeon.freship.orders.domain.contant.OrderStatus;
import me.yeon.freship.orders.infrastructure.OrderCodeGenerator;
import me.yeon.freship.orders.infrastructure.OrderRepository;
import me.yeon.freship.product.domain.Product;
import me.yeon.freship.product.infrastructure.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j @Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderCodeGenerator orderCodeGenerator;
    private final OrderRepository repository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    // TODO: 동시성 제어 필요
    public Long create(Long memberId, int orderAmount, Long productId) {
        // TODO: ErrorCode 변경
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ClientException(ErrorCode.EXCEPTION));

        // TODO: ErrorCode 변경
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ClientException(ErrorCode.EXCEPTION));

        // product의 재고 차감

        String orderCode = orderCodeGenerator.create(product.getCategory(), getCurrentDate());

        log.info("orderCode={}", orderCode);
        return repository.save(Order.newOrder(orderCode, member, product, orderAmount))
                .getId();
    }


    public void cancel(Long orderId) {
        // TODO: ErrorCode 변경
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new ClientException(ErrorCode.EXCEPTION));

        // Order의 상태가 배송 시작, 배송 완료일 때에는 취소할 수 없음.

        // 재고를 다시 늘려야 함.

        order.changeStatus(OrderStatus.CANCEL);
    }

    public void startDelivery(Long orderId) {
        // TODO: ErrorCode 변경
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new ClientException(ErrorCode.EXCEPTION));

        // '상품 준비중' 상태에서만 배송 시작 가능
        if (order.getStatus() != OrderStatus.DELI_PROVISION) {
            throw new ClientException(ErrorCode.INVALID_REQ_DELIVERY);
        }

        order.startDelivery(
                LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault())
        );
    }

    public void paymentDone(Long orderId) {
        // TODO: ErrorCode 변경
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new ClientException(ErrorCode.EXCEPTION));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ClientException(ErrorCode.INVALID_ORDER_STATUS);
        }

        order.changeStatus(OrderStatus.DELI_PROVISION);
    }

    @Transactional(readOnly = true)
    public CustomerOrderInfo findOneByCustomer(Long orderId) {
        // TODO: ErrorCode 변경
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new ClientException(ErrorCode.EXCEPTION));
        return CustomerOrderInfo.fromOrder(order);
    }

    @Transactional(readOnly = true)
    public OwnerOrderInfo findOneByOwner(Long orderId) {
        // TODO: ErrorCode 변경
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new ClientException(ErrorCode.EXCEPTION));

        return OwnerOrderInfo.fromOrder(order);
    }

    @Transactional(readOnly = true)
    public Page<CustomerOrderInfo> findAllByCustomer(int pageNum, int pageSize) {
        Pageable pageRequest = PageRequest.of(pageNum, pageSize);
        return repository.findAllByCustomer(pageRequest);
    }

    @Transactional(readOnly = true)
    public Page<OwnerOrderInfo> findAllByOwner(int pageNum, int pageSize) {
        Pageable pageRequest = PageRequest.of(pageNum, pageSize);
        return repository.findAllByOwner(pageRequest);
    }


    private LocalDate getCurrentDate() {
        return Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

}
