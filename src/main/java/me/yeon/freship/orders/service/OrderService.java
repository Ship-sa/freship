package me.yeon.freship.orders.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.yeon.freship.common.domain.constant.ErrorCode;
import me.yeon.freship.common.exception.ClientException;
import me.yeon.freship.common.infrastructure.ClockHolder;
import me.yeon.freship.common.infrastructure.RedisLockRepository;
import me.yeon.freship.member.domain.Member;
import me.yeon.freship.member.domain.MemberRole;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Slf4j @Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderCodeGenerator orderCodeGenerator;
    private final ClockHolder clockHolder;

    private final OrderRepository repository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final RedisLockRepository redisLockRepository;

    @Transactional
    @Secured(MemberRole.Authority.MEMBER)
    public Long create(Long memberId, int orderAmount, Long productId) throws InterruptedException {
        while (!redisLockRepository.lock(productId.toString())) {
            Thread.sleep(5000);
        }

        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ClientException(ErrorCode.PRODUCT_NOT_FOUND));
            // product의 재고 차감
            if (product.getQuantity() - orderAmount < 0) {
                throw new ClientException(ErrorCode.LACK_OF_QUANTITY);
            }
            String orderCode = orderCodeGenerator.create(product.getCategory(), getCurrentDate());

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new ClientException(ErrorCode.NOT_FOUND_MEMBER));
            product.decreaseQuantity(orderAmount);

            return repository
                    .save(Order.newOrder(orderCode, member, product, orderAmount))
                    .getId();
        } finally {
            redisLockRepository.unlock(productId.toString());
        }
    }

    @Transactional
    @Secured(MemberRole.Authority.MEMBER)
    public Long cancel(Long orderId, Long memberId) {
        Order order = repository.findByIdWithMember(orderId)
                .orElseThrow(() -> new ClientException(ErrorCode.NOT_FOUND_ORDER));

        // 주문의 상태가 배송 시작, 배송 완료일 때에는 취소할 수 없음.
        if (order.getStatus() == OrderStatus.DELI_PROGRESS || order.getStatus() == OrderStatus.DELI_DONE) {
            throw new ClientException(ErrorCode.INVALID_ORDER_STATUS);
        }

        Product product = productRepository.findByIdWithStoreAndOwner(order.getId())
                .orElseThrow(() -> new ClientException(ErrorCode.PRODUCT_NOT_FOUND));

        // 자신과 관련된 주문만 변경할 수 있음
        if (!memberId.equals(order.getMember().getId()) && !memberId.equals(product.getStore().getMember().getId())) {
            throw new ClientException(ErrorCode.FORBIDDEN_ORDER_CANCELLATION);
        }

        // 재고를 늘리고 상태를 결제 취소로 바꿈.
        product.increaseQuantity(order.getOrderCount());
        order.changeStatus(OrderStatus.CANCEL);

        return order.getId();
    }

    @Transactional
    @Secured(MemberRole.Authority.OWNER)
    public void startDelivery(Long orderId, Long ownerId) {
        Order order = repository.findByIdAndOwnerId(orderId, ownerId)
                .orElseThrow(() -> new ClientException(ErrorCode.FORBIDDEN_DELI_START));

        // '상품 준비중' 상태에서만 배송 시작 가능
        if (order.getStatus() != OrderStatus.DELI_PROVISION) {
            throw new ClientException(ErrorCode.INVALID_REQ_DELIVERY);
        }

        order.startDelivery(clockHolder.currentLocalDateTime());
    }

    @Transactional
    public void paymentDone(String orderCode) {
        Order order = repository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ClientException(ErrorCode.NOT_FOUND_ORDER));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ClientException(ErrorCode.INVALID_ORDER_STATUS);
        }

        order.changeStatus(OrderStatus.DELI_PROVISION);
    }

    @Transactional(readOnly = true)
    @Secured(MemberRole.Authority.MEMBER)
    public CustomerOrderInfo findOneByCustomer(Long memberId, Long orderId) {
        Order order = repository.findByIdWithMember(orderId)
                .orElseThrow(() -> new ClientException(ErrorCode.NOT_FOUND_ORDER));

        if (memberId.equals(order.getId())) {
            throw new ClientException(ErrorCode.FORBIDDEN_ORDER_VIEW);
        }

        return CustomerOrderInfo.fromOrder(order);
    }

    @Transactional(readOnly = true)
    @Secured(MemberRole.Authority.MEMBER)
    public Page<CustomerOrderInfo> findAllByCustomer(Long memberId, int pageNum, int pageSize) {
        Pageable pageRequest = PageRequest.of(pageNum - 1, pageSize);
        return repository.findAllByCustomer(pageRequest, memberId);
    }

    @Transactional(readOnly = true)
    @Secured(MemberRole.Authority.OWNER)
    public Page<OwnerOrderInfo> findAllByOwner(Long memberId, int pageNum, int pageSize) {
        Pageable pageRequest = PageRequest.of(pageNum - 1, pageSize);
        return repository.findAllByOwner(pageRequest, memberId);
    }


    private LocalDate getCurrentDate() {
        return Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

}
